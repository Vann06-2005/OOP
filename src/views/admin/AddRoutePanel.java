package views.admin;

import controllers.RouteController;
import controllers.ScheduleController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import models.Route;

public class AddRoutePanel extends JPanel {
    private static final String[] CAMBODIA_PROVINCES = {
            "Banteay Meanchey",
            "Battambang",
            "Kampong Cham",
            "Kampong Chhnang",
            "Kampong Speu",
            "Kampong Thom",
            "Kampot",
            "Kandal",
            "Kep",
            "Koh Kong",
            "Kratie",
            "Mondulkiri",
            "Oddar Meanchey",
            "Pailin",
            "Phnom Penh",
            "Preah Vihear",
            "Prey Veng",
            "Pursat",
            "Ratanakiri",
            "Siem Reap",
            "Sihanoukville",
            "Stung Treng",
            "Svay Rieng",
            "Takeo",
            "Tboung Khmum"
    };

    private JComboBox<String> srcField, dstField;
    private JTextField distField, durField;
    private final JTable routeTable;
    private final DefaultTableModel tableModel;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn;
    private List<Route> routes = new ArrayList<>();
    private Route selectedRoute;

    public AddRoutePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Source City:"), gbc);
        srcField = new JComboBox<>(CAMBODIA_PROVINCES);
        srcField.setSelectedIndex(-1);
        gbc.gridx = 1;
        formPanel.add(srcField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination City:"), gbc);
        dstField = new JComboBox<>(CAMBODIA_PROVINCES);
        dstField.setSelectedIndex(-1);
        gbc.gridx = 1;
        formPanel.add(dstField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Distance (km):"), gbc);
        distField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(distField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Duration (e.g., 4h 30m):"), gbc);
        durField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(durField, gbc);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        addBtn = new JButton("Add Route");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");
        refreshBtn = new JButton("Refresh");
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        buttonRow.add(addBtn);
        buttonRow.add(updateBtn);
        buttonRow.add(deleteBtn);
        buttonRow.add(clearBtn);
        buttonRow.add(refreshBtn);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonRow, gbc);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Source", "Destination", "Distance (km)", "Duration"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        routeTable = new JTable(tableModel);
        routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                syncSelectionFromTable();
            }
        });
        JScrollPane scrollPane = new JScrollPane(routeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearSelection());
        refreshBtn.addActionListener(e -> loadRoutes());

        loadRoutes();
    }

    private void handleAdd() {
        try {
            String src = (String) srcField.getSelectedItem();
            String dst = (String) dstField.getSelectedItem();
            double dist = Double.parseDouble(distField.getText().trim());
            String dur = durField.getText().trim();

            if (src == null || dst == null || dur.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            Route r = new Route(null, src, dst, dist, dur);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    RouteController.getInstance().addRoute(r);
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(AddRoutePanel.this, "Route Added!");
                    clearSelection();
                    loadRoutes();
                }
            }.execute();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid distance. Please enter a number.");
        }
    }

    private void handleUpdate() {
        if (selectedRoute == null) {
            JOptionPane.showMessageDialog(this, "Select a route to update.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Update not implemented. Routes are immutable.");
    }

    private void handleDelete() {
        if (selectedRoute == null) {
            JOptionPane.showMessageDialog(this, "Select a route to delete.");
            return;
        }

        if (selectedRoute.getId() == null) {
            JOptionPane.showMessageDialog(this, "Selected route has no ID.");
            return;
        }

        int scheduleCount = ScheduleController.getInstance().countSchedulesByRoute(selectedRoute.getId());
        if (scheduleCount < 0) {
            JOptionPane.showMessageDialog(this, "Unable to check schedules for this route. Please try again.");
            return;
        }

        if (scheduleCount > 0) {
            Route replacementRoute = promptForReplacementRoute(scheduleCount);
            if (replacementRoute == null) {
                return;
            }

            String replacementLabel = formatRouteLabel(replacementRoute);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Move " + scheduleCount + " schedule(s) to " + replacementLabel + " and delete this route?",
                    "Confirm Reassign and Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            Long routeId = selectedRoute.getId();
            Long replacementId = replacementRoute.getId();
            new SwingWorker<Boolean, Void>() {
                private int reassignedCount;

                @Override
                protected Boolean doInBackground() {
                    reassignedCount = ScheduleController.getInstance()
                            .reassignSchedulesToRoute(routeId, replacementId);
                    if (reassignedCount < 0) {
                        return false;
                    }
                    return RouteController.getInstance().deleteRoute(routeId);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(AddRoutePanel.this,
                                    "Route deleted. " + reassignedCount + " schedule(s) moved to " + replacementLabel + ".");
                            clearSelection();
                            loadRoutes();
                        } else {
                            JOptionPane.showMessageDialog(AddRoutePanel.this,
                                    "Delete failed. Unable to move schedules or remove the route.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AddRoutePanel.this, "Error deleting route: " + ex.getMessage());
                    }
                }
            }.execute();
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete route " + selectedRoute.getSourceCity() + " -> " + selectedRoute.getDestinationCity() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return RouteController.getInstance().deleteRoute(selectedRoute.getId());
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AddRoutePanel.this, "Route deleted.");
                        clearSelection();
                        loadRoutes();
                    } else {
                        JOptionPane.showMessageDialog(AddRoutePanel.this, "Delete failed. Please try again.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AddRoutePanel.this, "Error deleting route: " + ex.getMessage());
                }
            }
        }.execute();
    }
    //
    private Route promptForReplacementRoute(int scheduleCount) {
        if (routes == null || routes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No routes are available to replace this one.");
            return null;
        }

        List<Route> candidates = new ArrayList<>();
        for (Route route : routes) {
            if (route.getId() != null && !route.getId().equals(selectedRoute.getId())) {
                candidates.add(route);
            }
        }

        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "This route is linked to " + scheduleCount + " schedule(s). Create another route before deleting.");
            return null;
        }

        String[] labels = new String[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            labels[i] = formatRouteLabel(candidates.get(i));
        }

        JComboBox<String> replacementBox = new JComboBox<>(labels);
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("This route is linked to " + scheduleCount + " schedule(s). Choose a new route:"), BorderLayout.NORTH);
        panel.add(replacementBox, BorderLayout.CENTER);

        int choice = JOptionPane.showConfirmDialog(this, panel, "Reassign Schedules",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return null;
        }
        return candidates.get(replacementBox.getSelectedIndex());
    }

    private String formatRouteLabel(Route route) {
        return "ID " + route.getId() + ": " + route.getSourceCity() + " -> " + route.getDestinationCity();
    }

    private void syncSelectionFromTable() {
        int selectedRow = routeTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < routes.size()) {
            selectedRoute = routes.get(selectedRow);
            srcField.setSelectedItem(selectedRoute.getSourceCity());
            dstField.setSelectedItem(selectedRoute.getDestinationCity());
            distField.setText(String.valueOf(selectedRoute.getDistanceKm()));
            durField.setText(selectedRoute.getEstimatedDuration());
            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        }
    }

    private void clearSelection() {
        selectedRoute = null;
        srcField.setSelectedIndex(-1);
        dstField.setSelectedIndex(-1);
        distField.setText("");
        durField.setText("");
        routeTable.clearSelection();
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    private void loadRoutes() {
        new SwingWorker<List<Route>, Void>() {
            @Override
            protected List<Route> doInBackground() {
                return RouteController.getInstance().getAllRoutes();
            }

            @Override
            protected void done() {
                try {
                    routes = get();
                    tableModel.setRowCount(0);
                    if (routes != null) {
                        for (Route route : routes) {
                            tableModel.addRow(new Object[]{
                                    route.getId(),
                                    route.getSourceCity(),
                                    route.getDestinationCity(),
                                    route.getDistanceKm(),
                                    route.getEstimatedDuration()
                            });
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AddRoutePanel.this, "Error loading routes: " + e.getMessage());
                }
            }
        }.execute();
    }
}
