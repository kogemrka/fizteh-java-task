package ru.fizteh.fivt.students.myhinMihail;

import ru.fizteh.fivt.bind.test.*;
import ru.fizteh.fivt.students.myhinMihail.xmlBinder.XmlBinder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.*;
import org.w3c.dom.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class UserListTable extends AbstractTableModel {
    private String[] columnsNames;
    private ArrayList<ArrayList<Object>> userData;

    public UserListTable(String[] names, ArrayList<ArrayList<Object>> users) {
        columnsNames = names;
        userData = users;
    }

    @Override
    public int getColumnCount() {
        return columnsNames.length;
    }

    @Override
    public int getRowCount() {
        return userData.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnsNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return userData.get(row).get(col);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        userData.get(row).set(col, value);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        userData.remove(row);
        fireTableDataChanged();
    }

    public void addRow(ArrayList<Object> row) {
        userData.add(row);
        fireTableDataChanged();
    }

    public ArrayList<ArrayList<Object>> getData() {
        return userData;
    }

    public void clear() {
        userData.clear();
        fireTableDataChanged();
    }
};


public class UserList extends JFrame {
    private JFrame frame = this;
    private JTable table;
    private XmlBinder<User> binder = new XmlBinder<User>(User.class);
    private File xmlFile;
    
    public class ActionRunner implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            switch (event.getActionCommand()) {
            
                case "OPEN": {
                    JFileChooser fileOpen = new JFileChooser();
                    fileOpen.setFileFilter(new FileNameExtensionFilter("Xml", "xml"));
                    fileOpen.setAcceptAllFileFilterUsed(false);
                    fileOpen.setDialogType(JFileChooser.OPEN_DIALOG);
                    if (fileOpen.showDialog(frame, "Open") == JFileChooser.APPROVE_OPTION) {
                        xmlFile = fileOpen.getSelectedFile();
                        if (!xmlFile.exists()) {
                            JOptionPane.showMessageDialog(frame, "Cannot open file '" + xmlFile.getName() + "'");
                            xmlFile = null;
                        } else {
                            loadUsers(xmlFile);
                        }
                    }
                    break;
                }
            
                case "SAVE": {
                    save();
                    break;
                }
            
                case "SAVE_AS": {
                    File prev = xmlFile;
                    JFileChooser fileSave = new JFileChooser();
                    fileSave.setFileFilter(new FileNameExtensionFilter("Xml", "xml"));
                    fileSave.setAcceptAllFileFilterUsed(false);
                    fileSave.setDialogType(JFileChooser.SAVE_DIALOG);
                    if (fileSave.showDialog(frame, "Save as") == JFileChooser.APPROVE_OPTION) {
                        xmlFile = new File(fileSave.getSelectedFile().getAbsolutePath()+".xml");
                        if (!save()) {
                            xmlFile = prev;
                        }
                    }
                    break;
                }
            
                case "NEW_USER": {
                    ArrayList<Object> vector = new ArrayList<>();
                    vector.add(0);
                    vector.add(UserType.USER);
                    vector.add(new String());
                    vector.add(new String());
                    vector.add(false);
                    vector.add(0);
                    ((UserListTable) table.getModel()).addRow(vector);
                    table.updateUI();
                    break;
                }
                
                case "DELETE_USER": {
                    int row = table.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(frame, "Select row for delete");
                        return;
                    }
                    ((UserListTable) table.getModel()).removeRow(row);
                    table.updateUI();
                    break;
                }
                
                case "EXIT": {
                    System.exit(0);
                }
            }
            
        };

        public boolean save() {
            if (xmlFile == null) {
                JOptionPane.showMessageDialog(frame, "No open file");
                return true;
            }
            try {
                ArrayList<User> usersList = new ArrayList<>();
                for (ArrayList<Object> list : ((UserListTable) table.getModel()).getData()) {
                    UserName name = new UserName((String) list.get(2), (String) list.get(3));
                    Permissions permissions = new Permissions();
                    permissions.setRoot((boolean) list.get(4));
                    permissions.setQuota((int) list.get(5));
                    
                    usersList.add(new User((int) list.get(0), (UserType) list.get(1), name, permissions));
                }
                saveUsers(usersList, xmlFile);
            } catch (Exception expt) {
                JOptionPane.showMessageDialog(frame, "Can not save file");
                return false;
            }
            
            return true;
        }
        
    }

    public UserList() {
        super("Xml User List");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 600);
        createMenu();
        createTable();
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new UserList();
        } catch (Exception expt) {
            System.exit(1);
        }
    }

    private void createMenu() {
        ActionRunner action = new ActionRunner();
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.setActionCommand("OPEN");
        fileOpen.addActionListener(action);
        file.add(fileOpen);
        
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.setActionCommand("SAVE");
        fileSave.addActionListener(action);
        file.add(fileSave);
        
        JMenuItem fileSaveAs = new JMenuItem("Save as");
        fileSaveAs.setActionCommand("SAVE_AS");
        fileSaveAs.addActionListener(action);
        file.add(fileSaveAs);
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.setActionCommand("EXIT");
        exit.addActionListener(action);
        file.add(exit);
        menu.add(file);
        
        JMenu edit = new JMenu("Edit");
        JMenuItem editNewUser = new JMenuItem("Add user");
        editNewUser.setActionCommand("NEW_USER");
        editNewUser.addActionListener(action);
        edit.add(editNewUser);
        
        JMenuItem editDeleteUser = new JMenuItem("Delete user");
        editDeleteUser.setActionCommand("DELETE_USER");
        editDeleteUser.addActionListener(action);
        edit.add(editDeleteUser);
        menu.add(edit);
        
        setJMenuBar(menu);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton btnOpen = new JButton("Open");
        btnOpen.setActionCommand("OPEN");
        btnOpen.addActionListener(action);
        toolBar.add(btnOpen);
        
        JButton btnSave = new JButton("Save");
        btnSave.setActionCommand("SAVE");
        btnSave.addActionListener(action);
        toolBar.add(btnSave);
        
        JButton btnNew = new JButton("Add");
        btnNew.setActionCommand("NEW_USER");
        btnNew.addActionListener(action);
        toolBar.add(btnNew);
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setActionCommand("DELETE_USER");
        btnDelete.addActionListener(action);
        toolBar.add(btnDelete);
        
        add(toolBar, BorderLayout.NORTH);
    }

    private void createTable() {
        String[] names = {"ID", "Type", "First name", "Last name", "Root", "Quota"};
        
        final DefaultCellEditor editor = new DefaultCellEditor(new JComboBox<>(UserType.values()));
        table = new JTable(new UserListTable(names, new ArrayList<ArrayList<Object>>())) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (convertColumnIndexToModel(column) == 1) {
                    return editor;
                } 
                
                return super.getCellEditor(row, column);
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return UserType.class;
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    case 4:
                        return Boolean.class;
                    case 5:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        
        table.setRowSorter(new TableRowSorter<>(table.getModel()));
        add(new JScrollPane(table));
    }
    
    public void loadUsers(File file) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement();
            if (!root.getTagName().equals("users")) {
                throw new RuntimeException("Unsupported xml file");
            }
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName().equals("user")) {
                    users.add((User) binder.getDeserializedValue((Element) node, User.class));
                }
            }
        } catch (Exception expt) {
            throw new RuntimeException("Cannot load file");
        }
        
        ((UserListTable) table.getModel()).clear();
        for (User user : users) {
            if (user == null) {
                continue;
            }
            ArrayList row = new ArrayList<>();
            row.add(user.getId());
            row.add(user.getUserType() == null ? UserType.USER : user.getUserType());
            
            UserName name = user.getName();
            if (name == null) {
                row.add(new String());
                row.add(new String());
            } else {
                row.add(name == null ? new String() : name.getFirstName());
                row.add(name == null ? new String() : name.getLastName());
            }
            
            Permissions permissions = user.getPermissions();
            if (permissions == null) {
                permissions = new Permissions();
            }
            row.add(permissions.isRoot());
            row.add(permissions.getQuota());
            ((UserListTable) table.getModel()).addRow(row);
        }
        table.updateUI();
    }
    
    public void saveUsers(ArrayList<User> users, File file) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlWriter.writeStartElement("users");
            for (User user : users) {
                xmlWriter.writeStartElement("user");
                binder.serializeToWriter(user, xmlWriter);
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        } catch (Exception expt) {
            throw new RuntimeException("Cannot save file");
        } finally {
            Utils.tryClose(writer);
        }
    }
}
