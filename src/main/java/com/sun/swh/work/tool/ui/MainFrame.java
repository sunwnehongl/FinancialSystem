package com.sun.swh.work.tool.ui;

import com.sun.swh.work.tool.service.ExcelToolService;
import com.sun.swh.work.tool.service.impl.ExcelToolServiceImpl;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @Auther: swh
 * @Date: 2019/8/3 22:05
 * @Description:
 */
public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JPanel dayReport;
    private JTextField chooseFild;
    private JButton chooseButton;
    private JButton saveButton;
    private JLabel chooseLabel;
    private JPanel purchase;
    private JPanel business;

    private ExcelToolService excelToolService = new ExcelToolServiceImpl();

    public MainFrame() {
        init();
        initDayReport();
    }

    private void init() {
        setTitle("小杨神器");
        setBounds(500, 100, 800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(mainPanel);
        setVisible(true);
    }

    private void initDayReport() {
        chooseButton.addActionListener(event ->{
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int choose = chooser.showOpenDialog(null);
            //判断选择的结果
            if(choose == JFileChooser.APPROVE_OPTION) {
                chooseFild.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        saveButton.addActionListener(e -> {
            String path = chooseFild.getText();
            if (StringUtils.isEmpty(path)) {
                JOptionPane.showMessageDialog(null, "请选择文件目录！");
                return;
            }
            SimpleDateFormat df = new SimpleDateFormat("YYYY-MM");
            path += "/" + df.format(new Date());
            excelToolService.createMothExcel(path);
            JOptionPane.showMessageDialog(null, "月度报表生成成功，请到目录"+path+"查看！");
        });
    }


    public static void main(String[] args) {
        new MainFrame();
    }


    private void createUIComponents() {
    }
}
