package com.example.onboardingschool

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.schoolerp.R

class OnboardingItemsAdapter(private val onboardingItems: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingItemsAdapter.OnboardingItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemsViewHolder {
        return OnboardingItemsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingItemsViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    inner class OnboardingItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgOnboarding: LottieAnimationView =
            view.findViewById<LottieAnimationView>(R.id.imgOnboarding)
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtDescription = view.findViewById<TextView>(R.id.txtDescription)

        fun bind(onboardingItem: OnboardingItem) {
            imgOnboarding.setAnimation(onboardingItem.onboardingImage)
            imgOnboarding.playAnimation()
            txtTitle.text = onboardingItem.title
            txtDescription.text = onboardingItem.description
        }
    }
}