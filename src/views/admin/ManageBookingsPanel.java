package views.admin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import controllers.BookingController;
import controllers.UserController;
import models.Booking;
import models.Schedule;

// Simple admin view for browsing and cancelling user bookings.
public class ManageBookingsPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable bookingTable;
    private final JButton loadAllBtn, refreshBtn, cancelBtn, deleteUserBtn;

    private List<Booking> bookings = new ArrayList<>();
    private Booking selectedBooking;

    public ManageBookingsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        loadAllBtn = new JButton("Load All");
        refreshBtn = new JButton("Refresh");
        cancelBtn = new JButton("Cancel Booking");
        deleteUserBtn = new JButton("Delete User");
        cancelBtn.setEnabled(false);
        deleteUserBtn.setEnabled(false);
        actionPanel.add(loadAllBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(deleteUserBtn);

        add(actionPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[] { "ID", "User", "Route", "Bus", "Departure", "Seat", "Amount", "Status", "Booked At" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                syncSelectionFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        loadAllBtn.addActionListener(e -> loadBookings());
        refreshBtn.addActionListener(e -> loadBookings());
        cancelBtn.addActionListener(e -> handleCancel());
        deleteUserBtn.addActionListener(e -> handleDeleteUser());

        loadBookings();
    }

    private void handleCancel() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel.");
            return;
        }
        if ("CANCELLED".equalsIgnoreCase(selectedBooking.getStatus())) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Cancel booking #" + selectedBooking.getId() + " for " + selectedBooking.getCustomer().getFullName()
                        + "?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        cancelBtn.setEnabled(false);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return BookingController.getInstance().cancelBooking(selectedBooking.getId());
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(ManageBookingsPanel.this, "Booking cancelled.");
                        loadBookings();
                    } else {
                        JOptionPane.showMessageDialog(ManageBookingsPanel.this,
                                "Unable to cancel booking. Please try again.");
                        cancelBtn.setEnabled(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManageBookingsPanel.this,
                            "Error cancelling booking: " + ex.getMessage());
                    cancelBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    private void handleDeleteUser() {
        if (selectedBooking == null || selectedBooking.getCustomer() == null) {
            JOptionPane.showMessageDialog(this, "Select a booking to choose which user to delete.");
            return;
        }
        Long userId = selectedBooking.getCustomer().getId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Selected booking does not have a user ID.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete user \"" + selectedBooking.getCustomer().getFullName() + "\" (ID: " + userId + ")?\n"
                        + "This will remove the user account.",
                "Confirm Delete User", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        deleteUserBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return UserController.getInstance().deleteUser(userId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(ManageBookingsPanel.this, "User deleted.");
                        loadBookings();
                    } else {
                        JOptionPane.showMessageDialog(ManageBookingsPanel.this,
                                "Unable to delete user. Please try again.");
                        deleteUserBtn.setEnabled(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManageBookingsPanel.this,
                            "Error deleting user: " + ex.getMessage());
                    deleteUserBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    private void syncSelectionFromTable() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < bookings.size()) {
            selectedBooking = bookings.get(selectedRow);
            cancelBtn.setEnabled(!"CANCELLED".equalsIgnoreCase(selectedBooking.getStatus()));
            deleteUserBtn.setEnabled(true);
        } else {
            selectedBooking = null;
            cancelBtn.setEnabled(false);
            deleteUserBtn.setEnabled(false);
        }
    }

    private void loadBookings() {
        cancelBtn.setEnabled(false);
        deleteUserBtn.setEnabled(false);
        new SwingWorker<List<Booking>, Void>() {
            @Override
            protected List<Booking> doInBackground() {
                return BookingController.getInstance().getAllBookings();
            }

            @Override
            protected void done() {
                try {
                    bookings = get();
                    populateTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ManageBookingsPanel.this,
                            "Error loading bookings: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (bookings == null) {
            return;
        }

        for (Booking booking : bookings) {
            Schedule schedule = booking.getSchedule();
            String routeLabel = schedule.getRoute().getSourceCity() + " -> "
                    + schedule.getRoute().getDestinationCity();
            tableModel.addRow(new Object[] {
                    booking.getId(),
                    booking.getCustomer().getFullName(),
                    routeLabel,
                    schedule.getBus().getBusNumber(),
                    schedule.getDepartureTime().format(fmt),
                    booking.getSeatNumber(),
                    booking.getTotalAmount(),
                    booking.getStatus(),
                    booking.getBookingDate().format(fmt)
            });
        }
    }
}
