package com.example.flowplayground.ui

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout

class AddAnimalDialog(context: Context) {
    companion object {
        private val nameId = View.generateViewId()
        private val cutenessId = View.generateViewId()
        private val barkId = View.generateViewId()
    }

    val view: View
    private val nameET: EditText
    private val cutenessET: EditText
    private val barkVolumeET: EditText

    init {
        view = RelativeLayout(context).apply {
            nameET = EditText(context).apply {
                this.id = nameId
                this.hint = "Name"
                this.inputType = InputType.TYPE_CLASS_TEXT
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    this.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    this.addRule(RelativeLayout.ALIGN_PARENT_START)
                }
            }.also(this::addView)

            cutenessET = EditText(context).apply {
                this.id = cutenessId
                this.hint = "Cuteness"
                this.inputType = InputType.TYPE_CLASS_NUMBER
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    this.addRule(RelativeLayout.BELOW, nameId)
                    this.addRule(RelativeLayout.ALIGN_PARENT_START)
                }
            }.also(this::addView)

            barkVolumeET = EditText(context).apply {
                this.id = barkId
                this.hint = "Bark Volume"
                this.inputType = InputType.TYPE_CLASS_NUMBER
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    this.addRule(RelativeLayout.BELOW, cutenessId)
                    this.addRule(RelativeLayout.ALIGN_PARENT_START)
                }
            }.also(this::addView)
        }
    }

    fun getName(): String = nameET.text.toString()
    fun getCuteness(): Int = cutenessET.text.toString().toInt()
    fun getBarkVolume(): Int = barkVolumeET.text.toString().toInt()
}