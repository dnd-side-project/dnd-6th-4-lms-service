package com.dnd.sixth.lmsservice.domain.repository

import com.dnd.sixth.lmsservice.data.model.feedbackComment.FeedBackModel
import com.dnd.sixth.lmsservice.domain.entity.DailyClassEntity


interface FeedBackCommentRepository {
    /*
    *  Feedback 업데이트
    *
    * */
    suspend fun updateFeedBack(feedBackModel: FeedBackModel): Number?

    /*
   *  Feedback 받아오기
   *
   * */
    suspend fun getFeedBackFromDailyClass(subjectId : Number, dailyId : Number) : DailyClassEntity?
}