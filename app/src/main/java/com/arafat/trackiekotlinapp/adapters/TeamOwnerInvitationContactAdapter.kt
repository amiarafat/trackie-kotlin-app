package com.arafat.trackiekotlinapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.model.Contact
import kotlinx.android.synthetic.main.list_each_row_team_owner_invitation_contact.view.*

class TeamOwnerInvitationContactAdapter(val context : Context,val contactArrayList : ArrayList<Contact> ,val onClickListener : MyAdapterListener) :
    RecyclerView.Adapter<TeamOwnerInvitationContactAdapter.ViewHolder>() {



    interface MyAdapterListener {
         fun btnRequestInviteOnClick( position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeamOwnerInvitationContactAdapter.ViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.list_each_row_team_owner_invitation_contact,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactArrayList.size
    }

    override fun onBindViewHolder(
        holder: TeamOwnerInvitationContactAdapter.ViewHolder,
        position: Int
    ) {
        val contact : Contact = contactArrayList.get(position)
        holder.tvContactName.text = contact.getName()
        holder.tvContactNumber.text = contact.getNumber()

        holder.btnInvite.setOnClickListener {
            onClickListener.btnRequestInviteOnClick(position)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tvContactName = itemView.tvContactName
        val tvContactNumber = itemView.tvContactNumber
        val btnInvite = itemView.btnAddContact



    }
}