package com.github.yuyuanweb.mianshiyaplugin.utils;

import com.github.yuyuanweb.mianshiyaplugin.config.GlobalState;
import com.github.yuyuanweb.mianshiyaplugin.constant.CommonConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.PageConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.TextConstant;
import com.github.yuyuanweb.mianshiyaplugin.view.LoginPanel;
import com.github.yuyuanweb.mianshiyaplugin.view.MTabModel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author pine
 */
public class PanelUtil {

    public static JPanel createClosePanel(String title, JPanel tabPanel, JBTabbedPane tabbedPane) {
        JPanel tabLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabLabel.setOpaque(false);

        JLabel tabTitle = new JLabel(title);
        tabLabel.add(tabTitle);

        JButton closeButton = new JButton(AllIcons.Actions.Close);
        closeButton.setPreferredSize(new Dimension(16, 16));
        closeButton.setBorder(BorderFactory.createCompoundBorder());
        closeButton.setContentAreaFilled(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 使用组件引用动态获取索引
                int tabIndex = tabbedPane.indexOfComponent(tabPanel);
                if (tabIndex >= 0) {
                    tabbedPane.remove(tabIndex);
                }
            }
        });

        // 添加鼠标事件监听器以处理悬浮效果
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setOpaque(true);
                closeButton.setBackground(JBColor.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setOpaque(false);
                closeButton.setBackground(null);
            }
        });

        tabLabel.add(closeButton);

        return tabLabel;
    }

    public static JBTable createTablePanel(MTabModel tableModel, BiConsumer<JBTable, MouseEvent> consumer, int columnIndex) {
        // 创建表格
        JBTable table = new JBTable(tableModel);
        table.setFillsViewportHeight(true);

        // 鼠标双击事件监听
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    consumer.accept(table, mouseEvent);
                }
            }
        });

        // 自定义单元格渲染器
        TableCellRenderer categoryRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    JBLabel jbLabel = new JBLabel();
                    jbLabel.setText(list.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("  ")));
                    jbLabel.setOpaque(true); // 确保背景颜色生效
                    jbLabel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    jbLabel.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    return jbLabel;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(categoryRenderer);

        // 设置列宽为0，使列存在但不可见
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        table.setFillsViewportHeight(true);

        return table;
    }

    public static void updatePaginationPanel(JBPanel<?> paginationPanel, long total, int[] currentPage, BiConsumer<Integer, Integer> loadPage) {
        paginationPanel.removeAll();
        int pageSize = Objects.requireNonNull(GlobalState.getInstance().getState()).pageSize;
        long totalPage = (total / pageSize) + 1;

        JBLabel pageLabel = new JBLabel("第 " + currentPage[0] + " 页，共 " + totalPage + " 页");

        JButton prevButton = new JButton("上一页");
        JButton nextButton = new JButton("下一页");

        prevButton.setEnabled(currentPage[0] > 1);
        nextButton.setEnabled(currentPage[0] < totalPage);

        prevButton.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                loadPage.accept(currentPage[0], pageSize);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage[0] < totalPage) {
                currentPage[0]++;
                loadPage.accept(currentPage[0], pageSize);
            }
        });

        ComboBox<Integer> pageSizeComboBox = new ComboBox<>();
        for (int size : PageConstant.PAGE_SIZES) {
            pageSizeComboBox.addItem(size);
        }
        pageSizeComboBox.setSelectedItem(pageSize);
        pageSizeComboBox.addActionListener(e -> {
            GlobalState.getInstance().getState().pageSize = (int) pageSizeComboBox.getSelectedItem();
            int newPageSize = GlobalState.getInstance().getState().pageSize;
            pageSizeComboBox.setSelectedItem(newPageSize);
            // 重置为第一页
            currentPage[0] = 1;
            loadPage.accept(currentPage[0], newPageSize);
        });

        paginationPanel.add(new JBLabel("每页:"));
        paginationPanel.add(pageSizeComboBox);
        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    public static JBPanel<?> getNeedVipPanel() {
        JBPanel<?> needVipPanel = new JBPanel<>();
        AbstractAction needVipAction = new AbstractAction("仅会员可见内容，请先开通会员", AllIcons.General.User) {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrowserUtil.browse(CommonConstant.VIP);
            }
        };
        needVipPanel.add(new JBOptionButton(needVipAction, null));
        return needVipPanel;
    }

    public static JBPanel<?> getNeedLoginPanel() {
        JBPanel<?> needLoginPanel = new JBPanel<>();
        AbstractAction needVipAction = new AbstractAction(TextConstant.LOGIN, AllIcons.General.User) {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginPanel loginPanel = new LoginPanel(ProjectManager.getInstance().getDefaultProject());
                loginPanel.show();
            }
        };
        needLoginPanel.add(new JBOptionButton(needVipAction, null));
        return needLoginPanel;
    }

}
