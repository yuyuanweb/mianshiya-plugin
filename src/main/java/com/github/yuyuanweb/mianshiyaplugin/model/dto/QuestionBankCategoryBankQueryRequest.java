package com.github.yuyuanweb.mianshiyaplugin.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题库分类和题库关联查询请求
 *
 * @author pine
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankCategoryBankQueryRequest extends QuestionBankQueryRequest {

    /**
     * todo
     * 题库分类 id
     */
    // private Long questionBankCategoryId = 1821883295995125761L;
    private Long questionBankCategoryId = 1821462154457419778L;

    private static final long serialVersionUID = 1L;
}