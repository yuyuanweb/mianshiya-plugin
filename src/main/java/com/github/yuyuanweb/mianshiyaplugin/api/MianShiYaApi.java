package com.github.yuyuanweb.mianshiyaplugin.api;

import com.github.yuyuanweb.mianshiyaplugin.model.common.BaseResponse;
import com.github.yuyuanweb.mianshiyaplugin.model.common.Page;
import com.github.yuyuanweb.mianshiyaplugin.model.common.PageRequest;
import com.github.yuyuanweb.mianshiyaplugin.model.dto.*;
import com.github.yuyuanweb.mianshiyaplugin.model.response.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * 面试鸭接口
 *
 * @author pine
 */
public interface MianShiYaApi {

    /**
     * 获取登录用户信息
     */
    @GET("user/get/login")
    Call<BaseResponse<User>> getLoginUser();

    /**
     * 获取题库列表
     */
    @POST("questionBankCategory/list_questionBank")
    Call<BaseResponse<Page<QuestionBank>>> getQuestionBankList(
            @Body QuestionBankCategoryBankQueryRequest queryRequest
    );

    /**
     * 获取题目列表
     */
    @POST("question/list/page/vo")
    Call<BaseResponse<Page<Question>>> getQuestionList(
            @Body QuestionQueryRequest queryRequest
    );

    /**
     * 获取题目详情
     */
    @GET("question/get/vo")
    Call<BaseResponse<Question>> getQuestion(
            @Query("questionId") long questionId
    );

    /**
     * 获取回答列表
     */
    @POST("question_answer/list/by_question")
    Call<BaseResponse<Page<QuestionAnswer>>> listQuestionAnswerByQuestionId(
            @Body QuestionAnswerQueryRequest queryRequest
    );

    /**
     * 获取题库分类列表
     */
    @POST("questionBankCategory/list")
    Call<BaseResponse<List<QuestionBankCategory>>> listQuestionBankCategory(
            @Body PageRequest pageRequest
    );

    /**
     * 获取题库列表
     */
    @POST("question_bank/list/page/vo")
    Call<BaseResponse<Page<QuestionBank>>> listQuestionBankVoByPage(
            @Body QuestionBankQueryRequest queryRequest
    );

    /**
     * 获取标签分类列表
     */
    @POST("tagCategory/list")
    Call<BaseResponse<List<TagGroup>>> listTagCategory(
            @Body TagCategoryQueryRequest queryRequest
    );

    /**
     * 获取标签分类列表
     */
    @POST("user/logout")
    Call<BaseResponse<Boolean>> userLogout();

}
