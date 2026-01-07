package views;

import controllers.BusController;
import controllers.RouteController;
import controllers.ScheduleController;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import models.Bus;
import models.Route;
import models.Schedule;

public class AdminViews extends JPanel {
    private MainApp mainApp;

    public AdminViews(MainApp app) {
        this.mainApp = app;
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        header.add(new JLabel("Welcome, Admin"));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> mainApp.logout());
        header.add(logoutBtn);
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Bus", new AddBusPanel());
        tabs.addTab("Add Route", new AddRoutePanel());
        tabs.addTab("Add Schedule", new AddSchedulePanel());
        
        add(tabs, BorderLayout.CENTER);
    }

    // --- SUB-PANEL: ADD BUS ---
    static class AddBusPanel extends JPanel {
        private JTextField numberField, seatsField;
        private JComboBox<String> typeBox;
        private JButton addBtn;

        public AddBusPanel() {
            setLayout(new GridLayout(5, 2, 10, 10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            add(new JLabel("Bus Number (Plate):"));
            numberField = new JTextField();
            add(numberField);

            add(new JLabel("Total Seats:"));
            seatsField = new JTextField();
            add(seatsField);

            add(new JLabel("Type:"));
            typeBox = new JComboBox<>(new String[]{"AC Sleeper", "Seater", "Luxury Volvo"});
            add(typeBox);

            add(new JLabel("")); // spacer
            addBtn = new JButton("Save Bus");
            add(addBtn);

            addBtn.addActionListener(e -> {
                String num = numberField.getText();
                String seatsStr = seatsField.getText();
                String type = (String) typeBox.getSelectedItem();

                try {
                    int seats = Integer.parseInt(seatsStr);
                    Bus b = new Bus(null, num, seats, type);
                    
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            BusController.getInstance().addBus(b);
                            return null;
                        }
                        @Override
                        protected void done() {
                            JOptionPane.showMessageDialog(AddBusPanel.this, "Bus Added Successfully!");
                            numberField.setText("");
                            seatsField.setText("");
                        }
                    }.execute();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Seats must be a number.");
                }
            });
        }
    }

    // --- SUB-PANEL: ADD ROUTE ---
    static class AddRoutePanel extends JPanel {
        private JTextField srcField, dstField, distField, durField;
        private JButton addBtn;

        public AddRoutePanel() {
            setLayout(new GridLayout(6, 2, 10, 10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            add(new JLabel("Source City:"));
            srcField = new JTextField();
            add(srcField);

            add(new JLabel("Destination City:"));
            dstField = new JTextField();
            add(dstField);

            add(new JLabel("Distance (km):"));
            distField = new JTextField();
            add(distField);

            add(new JLabel("Duration (e.g., 4h 30m):"));
            durField = new JTextField();
            add(durField);

            add(new JLabel(""));
            addBtn = new JButton("Save Route");
            add(addBtn);

            addBtn.addActionListener(e -> {
                try {
                    String src = srcField.getText();
                    String dst = dstField.getText();
                    double dist = Double.parseDouble(distField.getText());
                    String dur = durField.getText();

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
                            srcField.setText(""); dstField.setText(""); distField.setText(""); durField.setText("");
                        }
                    }.execute();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid inputs.");
                }
            });
        }
    }

    // --- SUB-PANEL: ADD SCHEDULE ---
    static class AddSchedulePanel extends JPanel {
        private JComboBox<String> busCombo;
        private JComboBox<String> routeCombo;
        private JTextField dateField, priceField;
        private JButton loadDataBtn, addBtn;
        
        // Hold actual objects to link IDs
        private List<Bus> loadedBuses;
        private List<Route> loadedRoutes;

        public AddSchedulePanel() {
            setLayout(new GridLayout(6, 2, 10, 10));
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            add(new JLabel("1. Load Data First:"));
            loadDataBtn = new JButton("Refresh Bus/Route Lists");
            add(loadDataBtn);

            add(new JLabel("Select Bus:"));
            busCombo = new JComboBox<>();
            add(busCombo);

            add(new JLabel("Select Route:"));
            routeCombo = new JComboBox<>();
            add(routeCombo);

            add(new JLabel("Dep. Date (yyyy-MM-dd HH:mm):"));
            dateField = new JTextField(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            add(dateField);

            add(new JLabel("Price ($):"));
            priceField = new JTextField();
            add(priceField);

            add(new JLabel(""));
            addBtn = new JButton("Create Schedule");
            add(addBtn);

            loadDataBtn.addActionListener(e -> loadLists());
            addBtn.addActionListener(e -> createSchedule());
        }

        private void loadLists() {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    loadedBuses = BusController.getInstance().getAllBuses();
                    loadedRoutes = RouteController.getInstance().getAllRoutes();
                    return null;
                }
                @Override
                protected void done() {
                    busCombo.removeAllItems();
                    routeCombo.removeAllItems();
                    
                    if(loadedBuses != null) {
                        for(Bus b : loadedBuses) busCombo.addItem(b.getBusNumber() + " (" + b.getType() + ")");
                    }
                    if(loadedRoutes != null) {
                        for(Route r : loadedRoutes) routeCombo.addItem(r.getSourceCity() + " -> " + r.getDestinationCity());
                    }
                }
            }.execute();
        }

        private void createSchedule() {
            if (busCombo.getSelectedIndex() == -1 || routeCombo.getSelectedIndex() == -1) return;

            try {
                Bus selectedBus = loadedBuses.get(busCombo.getSelectedIndex());
                Route selectedRoute = loadedRoutes.get(routeCombo.getSelectedIndex());
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime depTime = LocalDateTime.parse(dateField.getText(), formatter);
                LocalDateTime arrTime = depTime.plusHours(4); // Simplified: add 4 hours auto
                BigDecimal price = new BigDecimal(priceField.getText());

                Schedule s = new Schedule(null, selectedBus, selectedRoute, depTime, arrTime, price);

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        ScheduleController.getInstance().addSchedule(s);
                        return null;
                    }
                    @Override
                    protected void done() {
                        JOptionPane.showMessageDialog(AddSchedulePanel.this, "Schedule Created!");
                    }
                }.execute();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Check date format and price.");
                ex.printStackTrace();
            }
        }
    }
}
