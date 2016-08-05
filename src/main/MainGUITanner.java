package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.Book;
import java.util.ArrayList;
import javax.swing.*;
import main.main;




public class MainGUITanner extends JFrame {

    main ctx;


    public MainGUITanner(main ctx) {
        this.ctx = ctx;

        initComponents();
    }

    private void initComponents() {
        checkBox1 = new JCheckBox();
        button1 = new JButton();
        checkBox2 = new JCheckBox();
        comboBox1 = new JComboBox();
        label1 = new JLabel();

        comboBox1.addItem("Select Leather Type...");
        comboBox1.addItem("Soft Leather 1GP");
        comboBox1.addItem("Hard Leather 3GP");

        //======== this ========
        Container contentPane = getContentPane();


        //---- checkBox1 ----
        checkBox1.setText("Walk to GE when out of money/hides");

        //---- button1 ----
        button1.setText("Save Settings");

        //---- checkBox2 ----
        checkBox2.setText("Logout when done");

        //---- label1 ----
        label1.setText("Cheese Queso Hide Tanner V1.0.0");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);

        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(button1, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(checkBox1, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(checkBox2, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label1)
                                .addGap(60, 60, 60))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkBox2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkBox1)
                                .addGap(18, 18, 18)
                                .addComponent(button1)
                                .addGap(13, 13, 13))
        );
        pack();
        setLocationRelativeTo(getOwner());

        button1.addActionListener((ActionEvent s) -> {

            setVars();

            this.setVisible(false);

        });

    }


    //Set the GUI control variables in the main class
    public void setVars() {

        boolean returnGE = checkBox1.isSelected();
        boolean returnLogout = checkBox1.isSelected();

        boolean[] dataSets = {returnGE, returnLogout};

        ctx.setUIVars(dataSets, comboBox1.getSelectedItem().toString());


    }


    private JCheckBox checkBox1;
    private JButton button1;
    private JCheckBox checkBox2;
    private JComboBox comboBox1;
    private JLabel label1;
}
