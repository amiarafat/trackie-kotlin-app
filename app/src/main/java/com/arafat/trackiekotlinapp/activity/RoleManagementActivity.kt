package com.arafat.trackiekotlinapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.helper.SharedPrefManager

class RoleManagementActivity : AppCompatActivity() {

    private lateinit var rlTeamMember : RelativeLayout
    private lateinit var rlTeamOwner : RelativeLayout

    lateinit var sharedPrefManager : SharedPrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_management)

        initView();

        sharedPrefManager = SharedPrefManager(this)

        rlTeamOwner.setOnClickListener{
            sharedPrefManager.setUserRole(AppConstant().USER_ROLE_TEAM_OWNER)
            val intent = Intent(this, TeamOwnerInvitationActivity::class.java)
            startActivity(intent)
            finish()
        }

        rlTeamMember.setOnClickListener{
            sharedPrefManager.setUserRole(AppConstant().USER_ROLE_TEAM_MEMBER)
            val intentTeamMember = Intent(this, TeamMemberInvitationActivity::class.java)
            startActivity(intentTeamMember)
            finish()
        }
    }

    private fun initView() {

        rlTeamOwner = findViewById(R.id.rlTeamOwner);
        rlTeamMember = findViewById(R.id.rlTeamMember);
    }


}
