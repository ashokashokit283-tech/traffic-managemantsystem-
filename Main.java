import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main {

    static String currentView = "";

    static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/traffic_system",
                "root",
                "dbms"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static void loadVehicle(DefaultTableModel model) {
        model.setColumnIdentifiers(new String[]{"ID","Owner","Vehicle No","Type"});
        model.setRowCount(0);

        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM Vehicle");
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("vehicle_id"),
                        rs.getString("owner_name"),
                        rs.getString("vehicle_no"),
                        rs.getString("type")
                });
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    static void loadViolation(DefaultTableModel model) {
        model.setColumnIdentifiers(new String[]{"ID","Vehicle No","Fine","Reason"});
        model.setRowCount(0);

        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM Violation");
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("violation_id"),
                        rs.getString("vehicle_no"),
                        rs.getInt("fine"),
                        rs.getString("reason")
                });
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Traffic Management System");
        frame.setSize(1100,550);
        frame.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        // LEFT PANEL
        JButton vehicleBtn = new JButton("Vehicle");
        JButton violationBtn = new JButton("Violation");

        JPanel left = new JPanel(new GridLayout(2,1,10,10));
        left.setPreferredSize(new Dimension(150,0));

        vehicleBtn.setBackground(Color.BLUE);
        violationBtn.setBackground(Color.ORANGE);

        vehicleBtn.setForeground(Color.WHITE);
        violationBtn.setForeground(Color.WHITE);

        left.add(vehicleBtn);
        left.add(violationBtn);

        // RIGHT PANEL
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");

        JPanel right = new JPanel(new GridLayout(3,1,10,10));
        right.setPreferredSize(new Dimension(150,0));

        add.setBackground(Color.GREEN);
        update.setBackground(Color.MAGENTA);
        delete.setBackground(Color.RED);

        for(JButton b : new JButton[]{add,update,delete}) {
            b.setForeground(Color.WHITE);
        }

        right.add(add);
        right.add(update);
        right.add(delete);

        frame.add(left, BorderLayout.WEST);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(right, BorderLayout.EAST);

        // BUTTONS

        vehicleBtn.addActionListener(e -> {
            currentView = "vehicle";
            loadVehicle(model);
        });

        violationBtn.addActionListener(e -> {
            currentView = "violation";
            loadViolation(model);
        });

        // ADD
        add.addActionListener(e -> {

            if(currentView.equals("")) {
                JOptionPane.showMessageDialog(frame,"Select table first!");
                return;
            }

            try {
                Connection c = getConnection();

                if(currentView.equals("vehicle")) {

                    JTextField id = new JTextField();
                    JTextField owner = new JTextField();
                    JTextField vno = new JTextField();
                    JTextField type = new JTextField();

                    JPanel p = new JPanel(new GridLayout(4,2));
                    p.add(new JLabel("ID")); p.add(id);
                    p.add(new JLabel("Owner")); p.add(owner);
                    p.add(new JLabel("Vehicle No")); p.add(vno);
                    p.add(new JLabel("Type")); p.add(type);

                    if(JOptionPane.showConfirmDialog(frame,p)==JOptionPane.OK_OPTION) {

                        PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO Vehicle(vehicle_id,owner_name,vehicle_no,type) VALUES (?,?,?,?)"
                        );

                        ps.setInt(1,Integer.parseInt(id.getText()));
                        ps.setString(2,owner.getText());
                        ps.setString(3,vno.getText());
                        ps.setString(4,type.getText());
                        ps.executeUpdate();

                        loadVehicle(model);
                    }

                } else {

                    JTextField id = new JTextField();
                    JTextField vno = new JTextField();
                    JTextField fine = new JTextField();
                    JTextField reason = new JTextField();

                    JPanel p = new JPanel(new GridLayout(4,2));
                    p.add(new JLabel("ID")); p.add(id);
                    p.add(new JLabel("Vehicle No")); p.add(vno);
                    p.add(new JLabel("Fine")); p.add(fine);
                    p.add(new JLabel("Reason")); p.add(reason);

                    if(JOptionPane.showConfirmDialog(frame,p)==JOptionPane.OK_OPTION) {

                        PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO Violation(violation_id,vehicle_no,fine,reason) VALUES (?,?,?,?)"
                        );

                        ps.setInt(1,Integer.parseInt(id.getText()));
                        ps.setString(2,vno.getText());
                        ps.setInt(3,Integer.parseInt(fine.getText()));
                        ps.setString(4,reason.getText());
                        ps.executeUpdate();

                        loadViolation(model);
                    }
                }

            } catch(Exception ex){ ex.printStackTrace(); }
        });

        // UPDATE
        update.addActionListener(e -> {

            if(currentView.equals("")) {
                JOptionPane.showMessageDialog(frame,"Select table first!");
                return;
            }

            try {
                Connection c = getConnection();

                if(currentView.equals("vehicle")) {

                    String id = JOptionPane.showInputDialog("Vehicle ID");

                    PreparedStatement ps = c.prepareStatement(
                            "UPDATE Vehicle SET owner_name=?, vehicle_no=?, type=? WHERE vehicle_id=?"
                    );

                    ps.setString(1,JOptionPane.showInputDialog("Owner"));
                    ps.setString(2,JOptionPane.showInputDialog("Vehicle No"));
                    ps.setString(3,JOptionPane.showInputDialog("Type"));
                    ps.setInt(4,Integer.parseInt(id));
                    ps.executeUpdate();

                    loadVehicle(model);

                } else {

                    String id = JOptionPane.showInputDialog("Violation ID");

                    PreparedStatement ps = c.prepareStatement(
                            "UPDATE Violation SET vehicle_no=?, fine=?, reason=? WHERE violation_id=?"
                    );

                    ps.setString(1,JOptionPane.showInputDialog("Vehicle No"));
                    ps.setInt(2,Integer.parseInt(JOptionPane.showInputDialog("Fine")));
                    ps.setString(3,JOptionPane.showInputDialog("Reason"));
                    ps.setInt(4,Integer.parseInt(id));
                    ps.executeUpdate();

                    loadViolation(model);
                }

            } catch(Exception ex){ ex.printStackTrace(); }
        });

        // DELETE
        delete.addActionListener(e -> {

            if(currentView.equals("")) {
                JOptionPane.showMessageDialog(frame,"Select table first!");
                return;
            }

            try {
                Connection c = getConnection();

                if(currentView.equals("vehicle")) {

                    String id = JOptionPane.showInputDialog("Vehicle ID");

                    PreparedStatement ps = c.prepareStatement(
                            "DELETE FROM Vehicle WHERE vehicle_id=?"
                    );

                    ps.setInt(1,Integer.parseInt(id));
                    ps.executeUpdate();

                    loadVehicle(model);

                } else {

                    String id = JOptionPane.showInputDialog("Violation ID");

                    PreparedStatement ps = c.prepareStatement(
                            "DELETE FROM Violation WHERE violation_id=?"
                    );

                    ps.setInt(1,Integer.parseInt(id));
                    ps.executeUpdate();

                    loadViolation(model);
                }

            } catch(Exception ex){ ex.printStackTrace(); }
        });

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadVehicle(model);
    }
}