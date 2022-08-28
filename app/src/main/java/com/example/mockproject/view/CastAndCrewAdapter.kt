package com.example.mockproject.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mockproject.Constant.APIConstant
import com.example.mockproject.Model.CastAndCrew
import com.example.mockproject.R
import com.squareup.picasso.Picasso

class CastAndCrewAdapter (
    private var mCastAndCrewList: ArrayList<CastAndCrew>
    ): RecyclerView.Adapter<CastAndCrewAdapter.CastAndCrewViewHolder>(){

//    fun updateList(castAndCrewList: ArrayList<CastAndCrew>){
//        this.mCastAndCrewList = castAndCrewList
//        notifyDataSetChanged()
//    }

    override fun getItemCount(): Int {
        return mCastAndCrewList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastAndCrewViewHolder {
        return CastAndCrewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cast_crew_item,parent,false))
    }

    override fun onBindViewHolder(holder: CastAndCrewViewHolder, position: Int){
        holder.bindDataCastAndCrew(mCastAndCrewList[position])
    }

    class CastAndCrewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var textName: TextView = itemView.findViewById(R.id.cast_crew_item_name_text)
        private var avatarImg: ImageView = itemView.findViewById(R.id.cast_crew_item_avatar_image)
        fun bindDataCastAndCrew(castAndCrew: CastAndCrew){
            val url = APIConstant.BASE_IMG_URL + castAndCrew.profilePath
            Picasso.get().load(url).error(R.drawable.shubarb).into(avatarImg)
            textName.text = castAndCrew.name
        }
    }


}