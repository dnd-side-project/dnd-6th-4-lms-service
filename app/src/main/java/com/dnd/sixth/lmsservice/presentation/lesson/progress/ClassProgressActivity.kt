package com.dnd.sixth.lmsservice.presentation.lesson.progress


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dnd.sixth.lmsservice.R
import com.dnd.sixth.lmsservice.databinding.ActivityClassProgressBinding
import com.dnd.sixth.lmsservice.presentation.adapter.recyclerAdapter.TimeLineAdapter
import com.dnd.sixth.lmsservice.presentation.base.BaseActivity
import com.dnd.sixth.lmsservice.presentation.feedback.WriteFeedBackActivity
import com.dnd.sixth.lmsservice.presentation.homework.HomeworkManageActivity
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClassProgressActivity : BaseActivity<ActivityClassProgressBinding, ClassProgressViewModel>(),
    View.OnClickListener {
    override val layoutResId: Int
        get() = R.layout.activity_class_progress

    //뷰모델 추가
    override val viewModel: ClassProgressViewModel by viewModel()



    //넘겨받아야할 SubjectID 임시로 0이라 해둠
    val subjectId = 1



    //액티비티 초기화 메서드
    override fun initActivity() {
        //
        with(binding) {
            viewModel = this@ClassProgressActivity.viewModel
        }

        //타임라인 리스트를 요청
        viewModel.getTimeLineList(subjectId)
        //유저 타입 확인
        viewModel.checkRoleModel()

        //어답터 추가
        var adapter = TimeLineAdapter() { feedbackBtn ->
            val intent = Intent(this@ClassProgressActivity, WriteFeedBackActivity::class.java)
            startActivity(intent)
        }
        with(binding) {




            recyclerviewForTimeLine.adapter = adapter
            recyclerviewForTimeLine.layoutManager = LinearLayoutManager(this@ClassProgressActivity)
            binding.recyclerviewForTimeLine.visibility = VISIBLE
            binding.emptyClassStatusImg.visibility = GONE

//            progressStartFeedbackBtn.setOnClickListener {
//                var intent = Intent(this@ClassProgressActivity, StartFeedBackActivity::class.java)
//                startActivityForResult(intent, REQUEST_CODE)
//            }
        }

        //역할 변수 확인
        viewModel.role.observe(this , Observer {

            checkRole(it)
        })



        viewModel.dailyClassList.observe(this, Observer {
            adapter.timeLineList = it
            if(it.size>0) {
                //order에 00월 00회차로 입력되어야 할 것 같음
                binding.classListStatus.text = "${it.get(it.size-1).classOrder} 수업까지 진행 했어요 (${it.size}회차)"
            }

            adapter.notifyDataSetChanged()
        })



        // 클릭리스너 추가
        with(binding) {
            progressInviteBtn.setOnClickListener(this@ClassProgressActivity)
            toolbarBtn.setOnClickListener { finish() }
            progressHomeworkBtn.setOnClickListener{
                var intent = Intent(this@ClassProgressActivity, HomeworkManageActivity::class.java)
                startActivity(intent)
            }
        }
    }


    /*
    유저 타입 확인. 학생 = 0, 선생님 = 1
     */
    fun checkRole(role : Int){
        if(role==0){
            //초대하기 버튼 숨기기
            binding.progressInviteBtn.visibility = GONE
            //숙제관리 버튼 보이기
            binding.progressHomeworkBtn.visibility = VISIBLE

        }else if(role == 1){
            //초대하기 버튼 보이기
            binding.progressInviteBtn.visibility = VISIBLE
            //숙제관리 버튼 숨기기
            binding.progressHomeworkBtn.visibility = GONE
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode != Activity.RESULT_OK) {
//                return
//            }
//            binding.recyclerviewForTimeLine.visibility = VISIBLE
//            binding.progressStartFeedbackBtn.visibility = GONE
//        }
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.progress_invite_btn -> invite() // 수업 초대 링크 생성
        }
    }

    private fun invite() {
        val userId = "kim1234" // Query로 사용할 유저 아이디 (Uid로 변경 가능성)

        // (Manifest에 설정한 scheme, host, path와 동일해야 함.)
        val invitationLink = "https://pingpongservice.page.link/invite?uid=$userId" // 생성할 다이나믹 링크

        val dynamicLink =
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(invitationLink))
                .setDomainUriPrefix("https://pingpongservice.page.link") // 파이어베이스 다이나믹 링크란에 설정한 Prefix 입력
                .setAndroidParameters(
                    AndroidParameters.Builder().build()
                )
                .buildShortDynamicLink()

        dynamicLink.addOnSuccessListener { task ->
            val inviteLink = task.shortLink!!
            sendInviteLink(inviteLink)
        }

    }


    private fun sendInviteLink(inviteLink: Uri) {
        val teacherName = "최기택"
        val inviteIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // 고정 text
            setPackage("com.kakao.talk") // 카카오톡 패키지 지정
            // 초대 코드 텍스트 지정
            putExtra(
                Intent.EXTRA_TEXT,
                "$teacherName 선생님이 수업에 초대하였습니다!\n[수업 링크] : $inviteLink"
            )
        }

        try {
            startActivity(inviteIntent) // 수업 초대를 위해 카카오톡 실행
        } catch (e: ActivityNotFoundException) {
            // 카카오톡이 설치되어 있지 않은 경우 예외 발생
            showToast("카카오톡이 설치되어 있지 않습니다.")
        }
    }

}