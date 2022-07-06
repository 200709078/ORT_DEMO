import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class FormAna extends JFrame {
    private JTable tableDB;
    private JButton btnYeniKayit;
    private JButton btnGuncelle;
    private JButton btnSil;
    private JButton btnListeleUst;
    private JTextField txtKurumKodu;
    private JTextField txtKurumAdi;
    private JPanel anaPanel;
    private JTextField txtAra;
    private JButton btnArama;
    private JButton btnListeleAlt;
    private JButton btnFotoEkle;
    static String url = "jdbc:sqlite:.//DB//bilgiler.db";
    static Connection conn = null;

    DefaultTableModel modelim = new DefaultTableModel();
    Object[] kolonlar = {"KURUM KODU", "KURUM ADI"};
    Object[] satirlar = new Object[2];

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FormAna f = new FormAna();
                f.setVisible(true);
            }
        });
    }

    public FormAna() {
        add(anaPanel);
        setSize(800, 600);
        setTitle("ORTACA DB");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        baglanListele();
        txtKurumKodu.setText(modelim.getValueAt(0, 0).toString());
        txtKurumAdi.setText(modelim.getValueAt(0, 1).toString());

        btnYeniKayit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtKurumAdi.getText().equals("") || txtKurumKodu.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "KURUM KODU VEYA KURUM ADI BOŞ OLAMAZ.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String kodu, adi;
                    kodu = txtKurumKodu.getText();
                    adi = txtKurumAdi.getText();
                    String kaydetSQL = "INSERT INTO kurumlar (kodu, adi) VALUES (" + kodu + ", '" + adi + "')";
                    sorguCalistir(kaydetSQL);
                    baglanListele();
                }
            }
        });
        tableDB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                txtKurumKodu.setText(modelim.getValueAt(tableDB.getSelectedRow(), 0).toString());
                txtKurumAdi.setText(modelim.getValueAt(tableDB.getSelectedRow(), 1).toString());
            }
        });
        btnGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtKurumAdi.getText().equals("") || txtKurumKodu.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "KURUM KODU VEYA KURUM ADI BOŞ OLAMAZ.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String kodu, adi;
                    kodu = txtKurumKodu.getText();
                    adi = txtKurumAdi.getText();
                    String guncelleSQL = "UPDATE kurumlar SET adi = '" + adi + "' WHERE kodu = " + kodu;
                    sorguCalistir(guncelleSQL);
                    baglanListele();
                }
            }
        });
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtKurumAdi.getText().equals("") || txtKurumKodu.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "KURUM KODU VEYA KURUM ADI BOŞ OLAMAZ.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String kodu;
                    kodu = txtKurumKodu.getText();
                    String silSQL = "DELETE FROM kurumlar WHERE kodu = " + kodu;
                    sorguCalistir(silSQL);
                    baglanListele();
                }
            }
        });
        btnListeleUst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                baglanListele();
            }
        });
        btnArama.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelim.setRowCount(0);
                String adi = txtAra.getText();
                String araSQL = "SELECT * FROM kurumlar WHERE adi LIKE '" + adi + "%'";
                ResultSet rs = listele(araSQL);

                try {
                    while (rs.next()) {
                        satirlar[0] = rs.getString("kodu");
                        satirlar[1] = rs.getString("adi");
                        modelim.addRow(satirlar);
                    }
                    tableDB.setModel(modelim);
                } catch (SQLException er) {
                    throw new RuntimeException(er);
                }
            }
        });
        btnListeleAlt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                baglanListele();
            }
        });
        btnFotoEkle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String kkoduVerisi, adiVerisi;
                kkoduVerisi = txtKurumKodu.getText();
                adiVerisi = txtKurumAdi.getText();

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException et) {
                    throw new RuntimeException(et);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        FormFoto ff = new FormFoto(kkoduVerisi, adiVerisi);
                        ff.setVisible(true);
                    }
                });
            }
        });
    }

    void baglanListele() {
        baglan();
        String listeleSQL = "select * from kurumlar";
        ResultSet rs = listele(listeleSQL);
        modelim.setColumnCount(0);
        modelim.setRowCount(0);
        modelim.setColumnIdentifiers(kolonlar);

        try {
            while (rs.next()) {
                satirlar[0] = rs.getString("kodu");
                satirlar[1] = rs.getString("adi");
                modelim.addRow(satirlar);
            }
            tableDB.setModel(modelim);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void baglan() {
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static ResultSet listele(String sorgu) {
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sorgu);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void sorguCalistir(String sorgu) {
        Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate(sorgu);
            JOptionPane.showMessageDialog(null, "İŞLEM BAŞARILI.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            if (e.getErrorCode() == 5) {
                JOptionPane.showMessageDialog(null, "Veritabanı kilitlendi. Lütfen programı kapatıp tekrar açın.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
            }

            if (e.getErrorCode() == 19) {
                JOptionPane.showMessageDialog(null, "Bu kayıt zaten var.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}