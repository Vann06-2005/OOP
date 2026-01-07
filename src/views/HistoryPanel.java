package views;

import controllers.BookingController;
import models.Booking;
import models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private User currentUser;
    private JTable table;
    private DefaultTableModel model;
    private JButton refreshBtn;

    public HistoryPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        
        String[] cols = {"ID", "Date", "Route", "Seat", "Status", "Price"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        refreshBtn = new JButton("Refresh History");
        add(refreshBtn, BorderLayout.SOUTH);
        
        refreshBtn.addActionListener(e -> loadHistory());
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        loadHistory();
    }

    private void loadHistory() {
        new SwingWorker<List<Booking>, Void>() {
            @Override
            protected List<Booking> doInBackground() {
                return BookingController.getInstance().getBookingsForUser(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Booking> list = get();
                    model.setRowCount(0);
                    for(Booking b : list) {
                        model.addRow(new Object[]{
                            b.getId(),
                            b.getBookingDate().toLocalDate(),
                            b.getSchedule().getRoute().getSourceCity() + "->" + b.getSchedule().getRoute().getDestinationCity(),
                            b.getSeatNumber(),
                            b.getStatus(),
                            b.getTotalAmount()
                        });
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}