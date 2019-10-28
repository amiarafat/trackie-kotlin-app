package com.arafat.trackiekotlinapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.model.Contact
import kotlinx.android.synthetic.main.each_row_multiple_invitation.view.*
import kotlinx.android.synthetic.main.list_each_row_team_owner_invitation_contact.view.*


    class MultipleContactInviteAdapter(val context: Context, val  contactArrayList : ArrayList<Contact>, val onClickListener : MyAdapterListener) :
        RecyclerView.Adapter<MultipleContactInviteAdapter.ViewHolder>() {

        interface MyAdapterListener{
            fun btnRequestInviteOnClick( position: Int)
        }

        private var active: Boolean = false
        private var pos: Int = 0

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MultipleContactInviteAdapter.ViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.each_row_multiple_invitation,parent,false)
            return MultipleContactInviteAdapter.ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contactArrayList.size
        }

        override fun onBindViewHolder(
            holder: MultipleContactInviteAdapter.ViewHolder,
            position: Int)
        {

            val contact = contactArrayList.get(position)
            holder.tvContactName.text = contact.getName()
            holder.tvContactNumber.text = contact.getNumber()

            if (active) {
                if (holder.adapterPosition == pos) {
                    holder.pbInviteUser.visibility = View.VISIBLE
                }
            } else {

                holder.pbInviteUser.visibility = View.GONE
            }

            if (contact.isStatus() === 0) {
                holder.ivStatus.visibility = View.VISIBLE
            } else if (contact.isStatus() === 1) {
                holder.ivStatus.visibility = View.VISIBLE
                holder.ivStatus.setImageResource(R.drawable.ic_check_ok)
                holder.btnCancel.visibility = View.GONE
            } else {
                holder.ivStatus.visibility = View.GONE
            }

            holder.btnCancel.setOnClickListener {

                onClickListener.btnRequestInviteOnClick(position)
            }

        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            val tvContactName = itemView.tvMContactName
            val tvContactNumber = itemView.tvMContactNumber
            val btnCancel = itemView.btnItemCancel
            val ivStatus = itemView.ivStatus
            val pbInviteUser = itemView.pbInviteUser

        }

        fun showProgresss(activie: Boolean, pos: Int) {


            this.active = activie
            this.pos = pos
            notifyDataSetChanged()
        }

    }