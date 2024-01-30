/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mlkitposebasic.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import android.widget.ToggleButton
import com.google.android.gms.common.annotation.KeepName
import com.example.mlkitposebasic.BAction.CameraSource
import com.example.mlkitposebasic.BAction.CameraSourcePreview
import com.example.mlkitposebasic.BAction.GraphicOverlay
import com.example.mlkitposebasic.R
import com.example.mlkitposebasic.BAction.kotlin.posedetector.PoseDetectorProcessor
import com.example.mlkitposebasic.BAction.preference.PreferenceUtils
import com.example.mlkitposebasic.BAction.preference.SettingsActivity
import com.example.mlkitposebasic.BAction.preference.SettingsActivity.LaunchSource
import java.io.IOException
import java.util.ArrayList

/** Live preview demo for ML Kit APIs. */
@KeepName
class BActionLeft :
  AppCompatActivity(), OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

  private var cameraSource: CameraSource? = null
  private var preview: CameraSourcePreview? = null
  private var graphicOverlay: GraphicOverlay? = null
  private var B1_POSE_SAMPLES_FILE: String = "pose/B-1_dump_poses_csvs_out.csv"
  private var B2_POSE_SAMPLES_FILE: String = "pose/B-2_dump_poses_csvs_out.csv"
  private var B3_POSE_SAMPLES_FILE: String = "pose/B-3_dump_poses_csvs_out.csv"
  private var B4_POSE_SAMPLES_FILE: String = "pose/B-4_dump_poses_csvs_out.csv"
  private var B5_POSE_SAMPLES_FILE: String = "pose/B-5_dump_poses_csvs_out.csv"
  private var B6_POSE_SAMPLES_FILE: String = "pose/B-6_dump_poses_csvs_out.csv"
  private var selectedModel = B1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "onCreate")
    setContentView(R.layout.activity_vision_live_preview)

    preview = findViewById(R.id.preview_view)
    if (preview == null) {
      Log.d(TAG, "Preview is null")
    }

    graphicOverlay = findViewById(R.id.graphic_overlay)
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null")
    }

    val spinner = findViewById<Spinner>(R.id.spinner)
    val options: MutableList<String> = ArrayList()
    options.add(B1)
    options.add(B2)
    options.add(B3)
    options.add(B4)
    options.add(B5)
    options.add(B6)


    // Creating adapter for spinner
    val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, options)

    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    // attaching data adapter to spinner
    spinner.adapter = dataAdapter
    spinner.onItemSelectedListener = this

    val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
    facingSwitch.setOnCheckedChangeListener(this)

    val settingsButton = findViewById<ImageView>(R.id.settings_button)
    settingsButton.setOnClickListener {
      val intent = Intent(applicationContext, SettingsActivity::class.java)
      intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, LaunchSource.LIVE_PREVIEW)
      startActivity(intent)
    }

    createCameraSource(selectedModel)
  }

  @Synchronized
  override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent?.getItemAtPosition(pos).toString()
    Log.d(TAG, "Selected model: $selectedModel")
    preview?.stop()
    createCameraSource(selectedModel)
    startCameraSource()
  }

  override fun onNothingSelected(parent: AdapterView<*>?) {
    // Do nothing.
  }

  override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
    Log.d(TAG, "Set facing")
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)
      } else {
        cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
      }
    }
    preview?.stop()
    startCameraSource()
  }

  private fun createCameraSource(model: String) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = CameraSource(this, graphicOverlay)
    }
    try {
      when (model) {
        B1 -> {
          val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
          Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
          val shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
          val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
          val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
          val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true,
              selectedModel,
              B1_POSE_SAMPLES_FILE
            )
          )
        } B2 -> {
          val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
          Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
          val shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
          val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
          val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
          val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true,
              selectedModel,
              B2_POSE_SAMPLES_FILE
            )
          )
        } B3 -> {
          val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
          Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
          val shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
          val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
          val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
          val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true,
              selectedModel,
              B3_POSE_SAMPLES_FILE
            )
          )
        } B4 -> {
          val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
          Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
          val shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
          val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
          val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
          val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true,
              selectedModel,
              B4_POSE_SAMPLES_FILE
            )
          )
        } B5 -> {
          val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
          Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
          val shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
          val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
          val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
          val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true,
              selectedModel,
              B5_POSE_SAMPLES_FILE
            )
          )
        } B6 -> {
          val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
          Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
          val shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
          val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
          val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
          val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true,
              selectedModel,
              B6_POSE_SAMPLES_FILE
            )
          )
        } else -> Log.e(TAG, "Unknown model: $model")
      }
    } catch (e: Exception) {
      Log.e(TAG, "Can not create image processor: $model", e)
      Toast.makeText(
          applicationContext,
          "Can not create image processor: " + e.message,
          Toast.LENGTH_LONG
        )
        .show()
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private fun startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null")
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null")
        }
        preview!!.start(cameraSource, graphicOverlay)
      } catch (e: IOException) {
        Log.e(TAG, "Unable to start camera source.", e)
        cameraSource!!.release()
        cameraSource = null
      }
    }
  }

  public override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume")
    createCameraSource(selectedModel)
    startCameraSource()
  }

  /** Stops the camera. */
  override fun onPause() {
    super.onPause()
    preview?.stop()
  }

  public override fun onDestroy() {
    super.onDestroy()
    if (cameraSource != null) {
      cameraSource?.release()
    }
  }

  companion object {
    private const val B1 = "팔 앞으로 올리기 스트레칭"
    private const val B2 = "어깨 모으기 스트레칭"
    private const val B3 = "팔 밖으로 돌리기 스트레칭"
    private const val B4 = "코너 스트레칭"
    private const val B5 = "옆으로 누워서 팔 안으로 돌리기 스트레칭"
    private const val B6 = "팔 뒤로 올리기 스트레칭"

    private const val TAG = "LivePreviewActivity"
  }
}
