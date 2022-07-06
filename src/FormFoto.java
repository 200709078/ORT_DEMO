import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class FormFoto extends JFrame {
    private JTable tableDB;
    private JButton btnYeniKayit;
    private JButton btnSil;
    private JTextField txtKurumKodu;
    private JPanel anaPanel;
    private JTextField txtFotoTuru;
    private JTextField txtFotoYolu;
    private JButton btnDosyaSec;
    private JTextField txtKurumAdi;
    private JLabel lblResim;
    static String url = "jdbc:sqlite:.//DB//bilgiler.db";
    static Connection conn = null;

    static DefaultTableModel modelim = new DefaultTableModel();
    Object[] kolonlar = {"FOTO ID", "KURUM KODU", "FOTO TÜRÜ", "FOTO YOLU"};
    Object[] satirlar = new Object[4];

    public FormFoto(String kkoduGelen, String adiGelen) {
        add(anaPanel);
        setSize(800, 600);
        setTitle("ORTACA DB");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        txtKurumKodu.setText(kkoduGelen);
        txtKurumAdi.setText(adiGelen);

        baglanListele(kkoduGelen);

        if (modelim.getRowCount() != 0) {
            txtFotoTuru.setText(modelim.getValueAt(0, 2).toString());
            txtFotoYolu.setText(modelim.getValueAt(0, 3).toString());
        }

        btnYeniKayit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String turu, yolu;
                turu = txtFotoTuru.getText();
                yolu = txtFotoYolu.getText();

                String kaydetSQL = "INSERT INTO fotolar (kkodu, turu, yolu) VALUES ('" + kkoduGelen + "', '" + turu + "', '" + yolu + "')";
                sorguCalistir(kaydetSQL);
                baglanListele(kkoduGelen);
            }
        });
        tableDB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtFotoTuru.setText(modelim.getValueAt(tableDB.getSelectedRow(), 2).toString());
                txtFotoYolu.setText(modelim.getValueAt(tableDB.getSelectedRow(), 3).toString());
                ImageIcon iconFoto = new ImageIcon("./Fotolar/" + txtFotoYolu.getText());

                Image imageFoto=iconFoto.getImage();
                Image modFoto =imageFoto.getScaledInstance(400,400,Image.SCALE_SMOOTH);

                ImageIcon icon = new ImageIcon(modFoto);
                lblResim.setIcon(icon);
            }
        });
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableDB.getSelectedRow() < 0) {
                    JOptionPane.showMessageDialog(null, "Lütfen tablodan bir seçim yapın.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String fotoid;
                    fotoid = modelim.getValueAt(tableDB.getSelectedRow(), 0).toString();
                    String silSQL = "DELETE FROM fotolar WHERE fotoid = " + fotoid;
                    sorguCalistir(silSQL);
                    baglanListele(kkoduGelen);
                }
            }
        });
        btnDosyaSec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableDB.getSelectedRow() < 0) {
                    JOptionPane.showMessageDialog(null, "Lütfen tablodan bir seçim yapın.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
                } else {

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("./Fotolar"));
                    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("IMAGE FILES", "jpg", "jpeg", "png"));
                    fileChooser.setAcceptAllFileFilterUsed(true);

                    int response = fileChooser.showOpenDialog(null);
                    if (response == JFileChooser.APPROVE_OPTION) {
                        File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                        txtFotoYolu.setText(file.getName());
                    }

                    String fotoid, kkodu, turu, yolu;
                    fotoid = modelim.getValueAt(tableDB.getSelectedRow(), 0).toString();
                    turu = txtFotoTuru.getText();
                    yolu = txtFotoYolu.getText();
                    String guncelleSQL = "UPDATE fotolar SET turu = '" + turu + "', yolu = '" + yolu + "' WHERE fotoid = " + fotoid;
                    sorguCalistir(guncelleSQL);
                    baglanListele(kkoduGelen);
                }
            }
        });
    }

    void baglanListele(String kkoduGelen) {
        baglan();
        String listeleSQL = "select * from fotolar WHERE kkodu = '" + kkoduGelen + "'";
        ResultSet rs = listele(listeleSQL);
        modelim.setColumnCount(0);
        modelim.setRowCount(0);
        modelim.setColumnIdentifiers(kolonlar);

        try {
            while (rs.next()) {
                satirlar[0] = rs.getString("fotoid");
                satirlar[1] = rs.getString("kkodu");
                satirlar[2] = rs.getString("turu");
                satirlar[3] = rs.getString("yolu");
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
            //JOptionPane.showMessageDialog(null, "İŞLEM BAŞARILI.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            if (e.getErrorCode() == 5) {
                JOptionPane.showMessageDialog(null, "Veritabanı kilitlendi. Lütfen programı kapatıp tekrar açın.", "ORTACA DB", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}