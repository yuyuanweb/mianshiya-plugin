package com.github.yuyuanweb.mianshiyaplugin.actions;

import com.github.yuyuanweb.mianshiyaplugin.config.ApiConfig;
import com.github.yuyuanweb.mianshiyaplugin.constant.TextConstant;
import com.github.yuyuanweb.mianshiyaplugin.model.common.Page;
import com.github.yuyuanweb.mianshiyaplugin.model.common.PageRequest;
import com.github.yuyuanweb.mianshiyaplugin.model.dto.QuestionBankCategoryBankQueryRequest;
import com.github.yuyuanweb.mianshiyaplugin.model.response.QuestionBank;
import com.github.yuyuanweb.mianshiyaplugin.model.response.QuestionBankCategory;
import com.github.yuyuanweb.mianshiyaplugin.utils.ContentUtil;
import com.github.yuyuanweb.mianshiyaplugin.utils.PanelUtil;
import com.github.yuyuanweb.mianshiyaplugin.view.MTabModel;
import com.github.yuyuanweb.mianshiyaplugin.view.QuestionListManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.*;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.WrapLayout;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author pine
 */
public class QuestionBankAction extends AnAction {

    private JBPanel<?> paginationPanel;
    private JBPanel<?> labelPanel;
    private MTabModel tableModel;
    private JBPanel<?> mainPanel;

    private final int[] currentPage = new int[]{1};

    public QuestionBankAction() {
    }

    public QuestionBankAction(String text, Icon icon) {
        super(text, text, icon);
    }

    public QuestionBankAction(JBPanel<?> mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 创建新的 tab 页
        JBPanel<?> tabPanel = new JBPanel<>(new BorderLayout());

        Project project = anActionEvent.getProject();
        if (mainPanel != null) {
            mainPanel.add(tabPanel, BorderLayout.CENTER);
        } else {
            ContentUtil.createContent(tabPanel, TextConstant.QUESTION_BANK, false, project);
        }

        // 标签栏
        this.loadLabelPanel();
        tabPanel.add(labelPanel, BorderLayout.NORTH);

        // 数据表格
        this.loadDataTable(tabPanel, project);

        // 分页条
        paginationPanel = new JBPanel<>(new GridBagLayout());
        tabPanel.add(paginationPanel, BorderLayout.SOUTH);
    }

    private Page<QuestionBank> fetchDataFromApi(QuestionBankCategoryBankQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new IllegalArgumentException("queryRequest cannot be null");
        }
        try {
            return ApiConfig.mianShiYaApi.getQuestionBankList(queryRequest).execute().body().getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadLabelPanel() {
        labelPanel = new JBPanel<>(new WrapLayout(FlowLayout.LEFT, 5, 5));
        ApplicationManager.getApplication().invokeLater(() -> {
            List<QuestionBankCategory> tagList = null;
            try {
                tagList = ApiConfig.mianShiYaApi.listQuestionBankCategory(new PageRequest()).execute().body().getData();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            for (QuestionBankCategory tag : tagList) {
                JBLabel label = new JBLabel(tag.getName());
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setOpaque(true);
                label.setBackground(JBColor.LIGHT_GRAY);
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // 点击事件
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        QuestionBankCategoryBankQueryRequest queryRequest = new QuestionBankCategoryBankQueryRequest();
                        queryRequest.setQuestionBankCategoryId(tag.getId());
                        searchAndLoadData(queryRequest);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // 鼠标悬浮效果
                        label.setBackground(JBColor.GRAY);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // 恢复原始背景色
                        label.setBackground(JBColor.LIGHT_GRAY);
                    }
                });

                labelPanel.add(label);
            }
        });
    }

    private void searchAndLoadData(QuestionBankCategoryBankQueryRequest queryRequest) {
        if (tableModel == null) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            // 清空现有表格数据
            tableModel.setRowCount(0);
            Page<QuestionBank> data = this.fetchDataFromApi(queryRequest);

            // 添加新数据到表格模型
            for (QuestionBank row : data.getRecords()) {
                tableModel.addRow(new Object[]{row.getId().toString(), row.getTitle(), row.getTagList()});
            }

            // 重新渲染表格
            tableModel.fireTableDataChanged();
            PanelUtil.updatePaginationPanel(paginationPanel, data.getTotal(), currentPage, this::loadPage);
        });
    }

    private void loadPage(int page, int pageSize) {
        // 实现分页逻辑，并更新表格数据
        QuestionBankCategoryBankQueryRequest queryRequest = new QuestionBankCategoryBankQueryRequest();
        queryRequest.setPageSize(pageSize);
        queryRequest.setCurrent(page);
        this.searchAndLoadData(queryRequest);
    }

    private void loadDataTable(JBPanel<?> tabPanel, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Page<QuestionBank> data = this.fetchDataFromApi(new QuestionBankCategoryBankQueryRequest());
            // 创建表格数据模型
            tableModel = new MTabModel();
            tableModel.addColumn("id");
            tableModel.addColumn("题库名称");
            tableModel.addColumn("所属分类");

            // 将数据添加到表格模型
            for (QuestionBank row : data.getRecords()) {
                tableModel.addRow(new Object[]{row.getId().toString(), row.getTitle(), row.getTagList()});
            }

            JBTable table = PanelUtil.createTablePanel(tableModel, (tempTable, mouseEvent) -> {
                int selectedRow = tempTable.getSelectedRow();
                if (selectedRow != -1) {
                    // 获取选中行的数据
                    String id = (String) tempTable.getValueAt(selectedRow, 0);

                    // 打开包含该行数据的新选项卡
                    QuestionListManager questionListManager = new QuestionListManager();
                    questionListManager.addQuestionTab(Long.valueOf(id), project);
                }
            }, 2);
            // 将表格添加到滚动面板
            JBScrollPane scrollPane = new JBScrollPane(table);
            // 确保表格充满视口
            scrollPane.setViewportView(table);
            tabPanel.add(scrollPane, BorderLayout.CENTER);

            // 更新分页条
            PanelUtil.updatePaginationPanel(paginationPanel, data.getTotal(), currentPage, this::loadPage);
        });
    }

}
