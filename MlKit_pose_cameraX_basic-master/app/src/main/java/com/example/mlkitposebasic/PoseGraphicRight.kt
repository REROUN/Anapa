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

package com.example.mlkitposebasic

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2
import java.util.Locale
import kotlin.math.*

/** Dibuja una pose detectada sobre una vista de tipo {@link GraphicOverlay}.
 * Define un descendiente de GraphicOverlay.Graphic que pueden ser añadidas a un {@link GraphicOverlay}.
 *
 * @param overlay vista donde se dibujará la pose
 * @param pose pose a representar
 * @param showInFrameLikelihood visualiza la probabilidad de cada punto
 * @param visualizeZ visualiza la profundidad (z) de cada línea (rojo->cerca, azul->lejos)
 * @param rescaleZForVisualiz si queremos que z se reescale entre valores max y min
 * @param drawUnlikelyLines las líneas entre puntos con un valor de FrameLikelihood bajo son dibujados
 *                           si es false y uno de los puntos de la línea tiene probabilidad <
 *                           IN_IN_FRAME_LIKELIHOOD, no se pinta la línea
 * */

class PoseGraphicRight internal constructor(
    overlay: GraphicOverlay_Aaction,
    private val pose: Pose,
    private val showInFrameLikelihood: Boolean = true,
    private val visualizeZ: Boolean  = true,
    private val rescaleZForVisualiz: Boolean  = true,
    private val drawUnlikelyLines: Boolean = true,
    private val selectedModel: String
) : GraphicOverlay_Aaction.Graphic(overlay) {

  private var zMin = java.lang.Float.MAX_VALUE
  private var zMax = java.lang.Float.MIN_VALUE
  private val leftPaint = Paint()
  private val rightPaint = Paint()
  private val whitePaint = Paint()
  private val bluePaint = Paint()
  private val greenPaint = Paint()

  init {

    whitePaint.strokeWidth = STROKE_WIDTH
    whitePaint.color = Color.WHITE
    whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
    greenPaint.strokeWidth = STROKE_WIDTH
    greenPaint.color = Color.GREEN
    greenPaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
    bluePaint.strokeWidth = STROKE_WIDTH
    bluePaint.color = Color.BLUE
    bluePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
    leftPaint.strokeWidth = STROKE_WIDTH
    leftPaint.color = Color.GREEN
    rightPaint.strokeWidth = STROKE_WIDTH
    rightPaint.color = Color.YELLOW
  }

  fun getAngle(firstPoint: PoseLandmark?, midPoint: PoseLandmark?, lastPoint: PoseLandmark?): Double {
    var result = Math.toDegrees((atan2(
      lastPoint!!.getPosition().y - midPoint!!.getPosition().y,
      lastPoint!!.getPosition().x - midPoint.getPosition().x)).toDouble()
            - atan2(
      firstPoint!!.getPosition().y - midPoint.getPosition().y,
      firstPoint.getPosition().x - midPoint.getPosition().x)).toDouble()
    result = Math.abs(result) // Angle should never be negative
    if (result > 180) {
      result = 360.0 - result // Always get the acute representation of the angle
    }
    return result
  }



  override fun draw(canvas: Canvas) { // Type 변수 받아오기
    val landmarks = pose.allPoseLandmarks
    if (landmarks.isEmpty()) return

    if (selectedModel == "앞으로 들기") {
      for (landmark in landmarks) { // Draw all the points
        if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_ELBOW
          || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
          || landmark.landmarkType == PoseLandmark.RIGHT_HIP ) {
          drawPoint(canvas, landmark, whitePaint)
        }
        if (visualizeZ && rescaleZForVisualiz) {
          zMin = zMin.coerceAtMost(landmark.position3D.z)
          zMax = zMax.coerceAtLeast(landmark.position3D.z)
        }
      }

      //val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
      val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
      //val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
      val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
      //val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
      val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
      //val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
      val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
      //val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
      //val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
      //val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
      //val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

      //drawLine(canvas, leftHombro, rightHombro, whitePaint)
      //drawLine(canvas, leftCadera, rightCadera, whitePaint)
      //drawLine(canvas, leftHombro, leftCodo, leftPaint)
      //drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
      //drawLine(canvas, leftHombro, leftCadera, leftPaint)
      //drawLine(canvas, leftCadera, leftRodilla, leftPaint)
      //drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
      drawLine(canvas, rightHombro, rightCodo, rightPaint)
      drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
      drawLine(canvas, rightHombro, rightCadera, rightPaint)
      //drawLine(canvas, rightCadera, rightRodilla, rightPaint)
      //drawLine(canvas, rightRodilla, rightTobillo, rightPaint)

      fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
      }


      // Draw inFrameLikelihood for all points
      if (showInFrameLikelihood) {
        for (landmark in landmarks) {
          if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
            val rightHipAnglerightHipAngle = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            )

            val accuracy = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            ) / 180 * 100

            whitePaint.setTextSize(100.0F)
            canvas.drawText(
              String.format(Locale.US, "%.0f", rightHipAnglerightHipAngle) + "°",
              translateX(landmark.position.x),
              translateY(landmark.position.y),
              whitePaint
            )

            bluePaint.setTextSize(70.0F)
            canvas.drawText(
              "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
              50F,
              100F,
              bluePaint
            )
          }
        }
      }
    } else if (selectedModel == "옆으로 들기") {

      // 각도 계산을 위한 백터 클래스정의
      data class Vector3D(val x: Float, val y: Float, val z: Float) {
        fun minus(other: Vector3D): Vector3D {
          return Vector3D(x - other.x, y - other.y, z - other.z)
        }

        fun dot(other: Vector3D): Float {
          return x * other.x + y * other.y + z * other.z
        }

        fun magnitude(): Float {
          return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
        }
      }

      //각도 계산함=-\][
      fun getAngle3D(firstPoint: PointF3D, midPoint: PointF3D, lastPoint: PointF3D): Double {
        val vector1 = Vector3D(lastPoint.x, lastPoint.y, lastPoint.z).minus(Vector3D(midPoint.x, midPoint.y, midPoint.z))
        val vector2 = Vector3D(firstPoint.x, firstPoint.y, firstPoint.z).minus(Vector3D(midPoint.x, midPoint.y, midPoint.z))


        val dotProduct = vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z
        val magnitudes = sqrt(vector1.x.pow(2) + vector1.y.pow(2) + vector1.z.pow(2)) * sqrt(vector2.x.pow(2) + vector2.y.pow(2) + vector2.z.pow(2))

        val angle = acos(dotProduct / magnitudes)
        return Math.toDegrees(angle.toDouble())
      }


      for (landmark in landmarks) { // Draw all the points
        if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_ELBOW
          || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
          || landmark.landmarkType == PoseLandmark.RIGHT_HIP ) {
          drawPoint(canvas, landmark, whitePaint)
        }
        if (visualizeZ && rescaleZForVisualiz) {
          zMin = zMin.coerceAtMost(landmark.position3D.z)
          zMax = zMax.coerceAtLeast(landmark.position3D.z)
        }
      }

      //val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
      val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
      //val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
      val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
      //val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
      val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
      //val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
      val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
      //val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
      //val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
      //val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
      //val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
      val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

      var modifiedRightHipX=0.0F
      var rightHipY=0.0F
      if (rightHombro != null && rightCadera != null) {
        modifiedRightHipX = rightHombro.getPosition().x
        rightHipY = rightCadera.getPosition().y
      }

//      drawLine(canvas, leftHombro, rightHombro, whitePaint)
//      drawLine(canvas, leftCadera, rightCadera, whitePaint)
//      drawLine(canvas, leftHombro, leftCodo, leftPaint)
//      drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
//      drawLine(canvas, leftHombro, leftCadera, leftPaint)
//      drawLine(canvas, leftCadera, leftRodilla, leftPaint)
//      drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
      drawLine(canvas, rightHombro, rightCodo, rightPaint)
      drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
      drawLine(canvas, rightHombro, rightCadera, rightPaint)


//      drawLine(canvas, rightCadera, rightRodilla, rightPaint)
//      drawLine(canvas, rightRodilla, rightTobillo, rightPaint)

      fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
      }


      // Draw inFrameLikelihood for all points
      if (showInFrameLikelihood) {
        for (landmark in landmarks) {
          if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
            val modifiedRightHipPosition = PointF3D.from(modifiedRightHipX, rightHip!!.getPosition().y, rightHip.getPosition3D().z)

//
//            val rightHipAnglerightHipAngle = getAngle(
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
//            )
//
//            val accuracy = getAngle(
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
//            ) / 180 * 100
            val rightHipAngle = getAngle3D(
              modifiedRightHipPosition,
              rightHombro!!.getPosition3D(),
              rightMuñeca!!.getPosition3D()
            )

            val accuracy = rightHipAngle / 180 * 100


            bluePaint.setTextSize(100.0F)
            canvas.drawText(
              String.format(Locale.US, "%.0f", rightHipAngle) + "°",
              translateX(landmark.position.x),
              translateY(landmark.position.y),
              bluePaint
            )

            bluePaint.setTextSize(70.0F)
            canvas.drawText(
              "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
              50F,
              100F,
              bluePaint
            )
          }
        }
      }
    } else if (selectedModel == "팔꿈치 외회전") {
      for (landmark in landmarks) { // Draw all the points
        if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
          || landmark.landmarkType == PoseLandmark.RIGHT_HIP) {
          drawPoint(canvas, landmark, whitePaint)
        }
        if (visualizeZ && rescaleZForVisualiz) {
          zMin = zMin.coerceAtMost(landmark.position3D.z)
          zMax = zMax.coerceAtLeast(landmark.position3D.z)
        }
      }

      //val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
      val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
      //val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
      val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
      //val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
      val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
      //val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
      val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
      //val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
      //val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
      //val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
      //val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

//      drawLine(canvas, leftHombro, rightHombro, whitePaint)
//      drawLine(canvas, leftCadera, rightCadera, whitePaint)
//      drawLine(canvas, leftHombro, leftCodo, leftPaint)
//      drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
//      drawLine(canvas, leftHombro, leftCadera, leftPaint)
//      drawLine(canvas, leftCadera, leftRodilla, leftPaint)
//      drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
      drawLine(canvas, rightHombro, rightCodo, rightPaint)
      drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
      drawLine(canvas, rightHombro, rightCadera, rightPaint)
//      drawLine(canvas, rightCadera, rightRodilla, rightPaint)
//      drawLine(canvas, rightRodilla, rightTobillo, rightPaint)

      canvas.drawLine(translateX(rightHombro!!.position3D.x), translateY(rightHombro!!.position3D.y), translateX(300.0f),
        translateY(rightHombro!!.position3D.y), rightPaint)
//      canvas.drawLine(translateX(start.x), translateY(start.y), translateX(end.x), translateY(end.y), paint)

      fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
      }


      // Draw inFrameLikelihood for all points
      if (showInFrameLikelihood) {
        for (landmark in landmarks) {
          if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
            var rightHipAnglerightHipAngle = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            ) - 90

            if (rightHipAnglerightHipAngle < 0) {
              rightHipAnglerightHipAngle = 0.0
            }

            val accuracy = rightHipAnglerightHipAngle / 90 * 100

            whitePaint.setTextSize(100.0F)
            canvas.drawText(
              String.format(Locale.US, "%.0f", rightHipAnglerightHipAngle) + "°외회전",
              translateX(landmark.position.x),
              translateY(landmark.position.y),
              whitePaint
            )

            bluePaint.setTextSize(70.0F)
            canvas.drawText(
              "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
              50F,
              100F,
              bluePaint
            )
          }
        }
      }
    } else if (selectedModel == "팔꿈치 내회전") {
      for (landmark in landmarks) { // Draw all the points
        if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
          || landmark.landmarkType == PoseLandmark.RIGHT_HIP ) {
          drawPoint(canvas, landmark, whitePaint)
        }
        if (visualizeZ && rescaleZForVisualiz) {
          zMin = zMin.coerceAtMost(landmark.position3D.z)
          zMax = zMax.coerceAtLeast(landmark.position3D.z)
        }
      }

      //val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
      val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
      //val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
      val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
      //val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
      val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
      //val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
      val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
//      val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
//      val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
//      val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
//      val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

//      drawLine(canvas, leftHombro, rightHombro, whitePaint)
//      drawLine(canvas, leftCadera, rightCadera, whitePaint)
//      drawLine(canvas, leftHombro, leftCodo, leftPaint)
//      drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
//      drawLine(canvas, leftHombro, leftCadera, leftPaint)
//      drawLine(canvas, leftCadera, leftRodilla, leftPaint)
//      drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
      drawLine(canvas, rightHombro, rightCodo, rightPaint)
      drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
      drawLine(canvas, rightHombro, rightCadera, rightPaint)
//      drawLine(canvas, rightCadera, rightRodilla, rightPaint)
//      drawLine(canvas, rightRodilla, rightTobillo, rightPaint)

      canvas.drawLine(translateX(rightHombro!!.position3D.x), translateY(rightHombro!!.position3D.y), translateX(300.0f),
        translateY(rightHombro!!.position3D.y), rightPaint)

      fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
      }


      // Draw inFrameLikelihood for all points
      if (showInFrameLikelihood) {
        for (landmark in landmarks) {
          if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
            var rightHipAnglerightHipAngle = 90 - getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            )

            if (rightHipAnglerightHipAngle < 0) {
              rightHipAnglerightHipAngle = 0.0
            }

            var accuracy = rightHipAnglerightHipAngle / 90 * 100

            whitePaint.setTextSize(100.0F)
            canvas.drawText(
              String.format(Locale.US, "%.0f", rightHipAnglerightHipAngle) + "°내회전",
              translateX(landmark.position.x),
              translateY(landmark.position.y),
              whitePaint
            )

            bluePaint.setTextSize(70.0F)
            canvas.drawText(
              "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
              50F,
              100F,
              bluePaint
            )
          }
        }
      }
    } else if (selectedModel == "내전") {
      for (landmark in landmarks) { // Draw all the points
        if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_ELBOW
          || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
          || landmark.landmarkType == PoseLandmark.LEFT_SHOULDER) {
          drawPoint(canvas, landmark, whitePaint)
        }
        if (visualizeZ && rescaleZForVisualiz) {
          zMin = zMin.coerceAtMost(landmark.position3D.z)
          zMax = zMax.coerceAtLeast(landmark.position3D.z)
        }
      }

      val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
      val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
      //val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
      val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
      //val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
      val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
//      val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
//      val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
//      val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
//      val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
//      val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
//      val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

      drawLine(canvas, leftHombro, rightHombro, whitePaint)
//      drawLine(canvas, leftCadera, rightCadera, whitePaint)
//      drawLine(canvas, leftHombro, leftCodo, leftPaint)
//      drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
//      drawLine(canvas, leftHombro, leftCadera, leftPaint)
//      drawLine(canvas, leftCadera, leftRodilla, leftPaint)
//      drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
      drawLine(canvas, rightHombro, rightCodo, rightPaint)
      drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
//      drawLine(canvas, rightHombro, rightCadera, rightPaint)
//      drawLine(canvas, rightCadera, rightRodilla, rightPaint)
//      drawLine(canvas, rightRodilla, rightTobillo, rightPaint)

      fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
      }


      // Draw inFrameLikelihood for all points
      if (showInFrameLikelihood) {
        for (landmark in landmarks) {
          if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
//            val rightHipAnglerightHipAngle = getAngle(
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
//              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
//            )

            val rightElbowrightShoulderleftShoulderAngle = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_ELBOW),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.LEFT_SHOULDER)
            )

            val accuracy = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            ) / 180 * 100

//            whitePaint.setTextSize(100.0F)
//            canvas.drawText(
//              String.format(Locale.US, "%.0f", rightHipAnglerightHipAngle) + "°",
//              translateX(landmark.position.x),
//              translateY(landmark.position.y),
//              whitePaint
//            )

            greenPaint.setTextSize(100.0F)
            canvas.drawText(
              String.format(Locale.US, "%.0f", rightElbowrightShoulderleftShoulderAngle) + "°",
              translateX(landmark.position.x) + 200,
              translateY(landmark.position.y),
              greenPaint
            )

//            bluePaint.setTextSize(70.0F)
//            canvas.drawText(
//              "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
//              50F,
//              100F,
//              bluePaint
//            )
          }
        }
      }
    } else if (selectedModel == "뒤로 들기(신전)") {
      for (landmark in landmarks) { // Draw all the points
        if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_ELBOW
          || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
          || landmark.landmarkType == PoseLandmark.RIGHT_HIP ) {
          drawPoint(canvas, landmark, whitePaint)
        }
        if (visualizeZ && rescaleZForVisualiz) {
          zMin = zMin.coerceAtMost(landmark.position3D.z)
          zMax = zMax.coerceAtLeast(landmark.position3D.z)
        }
      }

     // val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
      val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
      //val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
      val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
     // val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
      val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
      //val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
      val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
//      val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
//      val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
//      val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
//      val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

//      drawLine(canvas, leftHombro, rightHombro, whitePaint)
//      drawLine(canvas, leftCadera, rightCadera, whitePaint)
//      drawLine(canvas, leftHombro, leftCodo, leftPaint)
//      drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
//      drawLine(canvas, leftHombro, leftCadera, leftPaint)
//      drawLine(canvas, leftCadera, leftRodilla, leftPaint)
//      drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
      drawLine(canvas, rightHombro, rightCodo, rightPaint)
      drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
      drawLine(canvas, rightHombro, rightCadera, rightPaint)
//      drawLine(canvas, rightCadera, rightRodilla, rightPaint)
//      drawLine(canvas, rightRodilla, rightTobillo, rightPaint)

      fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
      }


      // Draw inFrameLikelihood for all points
      if (showInFrameLikelihood) {
        for (landmark in landmarks) {
          if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
            val rightHipAnglerightHipAngle = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            )

            val accuracy = getAngle(
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
              extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
            ) / 180 * 100

            whitePaint.setTextSize(100.0F)
            canvas.drawText(
              String.format(Locale.US, "%.0f", rightHipAnglerightHipAngle) + "°",
              translateX(landmark.position.x),
              translateY(landmark.position.y),
              whitePaint
            )

            bluePaint.setTextSize(70.0F)
            canvas.drawText(
              "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
              50F,
              100F,
              bluePaint
            )
          }
        }
      }
    }

//    for (landmark in landmarks) { // Draw all the points
//      if (landmark.landmarkType == PoseLandmark.RIGHT_WRIST || landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER
//        || landmark.landmarkType == PoseLandmark.RIGHT_HIP) {
//        drawPoint(canvas, landmark, whitePaint)
//      }
//      if (visualizeZ && rescaleZForVisualiz) {
//        zMin = zMin.coerceAtMost(landmark.position3D.z)
//        zMax = zMax.coerceAtLeast(landmark.position3D.z)
//      }
//    }
    //Puntos de la cara
//    val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
//    val lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
//    val lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
//    val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
//    val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
//    val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
//    val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)
//    val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
//    val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
//    val leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
//    val rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)
    //Puntos del tronco y extremidades
//    val leftHombro = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
//    val rightHombro = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
//    val leftCodo = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
//    val rightCodo = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
//    val leftMuñeca = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
//    val rightMuñeca = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
//    val leftCadera = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
//    val rightCadera = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
//    val leftRodilla = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
//    val rightRodilla = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
//    val leftTobillo = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
//    val rightTobillo = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
    //Puntos de manos y pies
//    val leftMeñique = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
//    val rightMeñique = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
//    val leftIndice = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
//    val rightIndice = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
//    val leftPulgar = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
//    val rightPulgar = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
//    val leftTacon = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
//    val rightTacon = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
//    val leftIndicePie = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
//    val rightIndicePie = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
    // Dibuja la cara
//    drawLine(canvas, nose, lefyEyeInner, whitePaint)
//    drawLine(canvas, lefyEyeInner, lefyEye, whitePaint)
//    drawLine(canvas, lefyEye, leftEyeOuter, whitePaint)
//    drawLine(canvas, leftEyeOuter, leftEar, whitePaint)
//    drawLine(canvas, nose, rightEyeInner, whitePaint)
//    drawLine(canvas, rightEyeInner, rightEye, whitePaint)
//    drawLine(canvas, rightEye, rightEyeOuter, whitePaint)
//    drawLine(canvas, rightEyeOuter, rightEar, whitePaint)
//    drawLine(canvas, leftMouth, rightMouth, whitePaint)
//
//    drawLine(canvas, leftHombro, rightHombro, whitePaint)
//    drawLine(canvas, leftCadera, rightCadera, whitePaint)
    // Dibuja cuerpo parte izquierda
//    drawLine(canvas, leftHombro, leftCodo, leftPaint)
//    drawLine(canvas, leftCodo, leftMuñeca, leftPaint)
//    drawLine(canvas, leftHombro, leftCadera, leftPaint)
//    drawLine(canvas, leftCadera, leftRodilla, leftPaint)
//    drawLine(canvas, leftRodilla, leftTobillo, leftPaint)
//    drawLine(canvas, leftMuñeca, leftPulgar, leftPaint)
//    drawLine(canvas, leftMuñeca, leftMeñique, leftPaint)
//    drawLine(canvas, leftMuñeca, leftIndice, leftPaint)
//    drawLine(canvas, leftIndice, leftMeñique, leftPaint)
//    drawLine(canvas, leftTobillo, leftTacon, leftPaint)
//    drawLine(canvas, leftTacon, leftIndicePie, leftPaint)
    // Dibuja cuerpo parte derecha
//    drawLine(canvas, rightHombro, rightCodo, rightPaint)
//    drawLine(canvas, rightCodo, rightMuñeca, rightPaint)
//    drawLine(canvas, rightHombro, rightCadera, rightPaint)
//    drawLine(canvas, rightCadera, rightRodilla, rightPaint)
//    drawLine(canvas, rightRodilla, rightTobillo, rightPaint)
//    drawLine(canvas, rightMuñeca, rightPulgar, rightPaint)
//    drawLine(canvas, rightMuñeca, rightMeñique, rightPaint)
//    drawLine(canvas, rightMuñeca, rightIndice, rightPaint)
//    drawLine(canvas, rightIndice, rightMeñique, rightPaint)
//    drawLine(canvas, rightTobillo, rightTacon, rightPaint)
//    drawLine(canvas, rightTacon, rightIndicePie, rightPaint)

//    fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
//      return pose.getPoseLandmark(landmarkType)
//    }


    // Draw inFrameLikelihood for all points
//    if (showInFrameLikelihood) {
//      for (landmark in landmarks) {
//        if (landmark.landmarkType == PoseLandmark.RIGHT_SHOULDER) {
//          val rightHipAnglerightHipAngle = getAngle(
//            extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
//            extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
//            extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
//          )
//
//          val accuracy = getAngle(
//            extractLandmarkFromType(pose, PoseLandmark.RIGHT_HIP),
//            extractLandmarkFromType(pose, PoseLandmark.RIGHT_SHOULDER),
//            extractLandmarkFromType(pose, PoseLandmark.RIGHT_WRIST)
//          ) / 180 * 100
//
//          whitePaint.setTextSize(100.0F)
//          canvas.drawText(
//            String.format(Locale.US, "%.0f", rightHipAnglerightHipAngle) + "°",
//            translateX(landmark.position.x),
//            translateY(landmark.position.y),
//            whitePaint
//          )
//
//          bluePaint.setTextSize(70.0F)
//          canvas.drawText(
//            "정확도 : " + String.format(Locale.US, "%.0f", accuracy) + "%",
//            50F,
//            100F,
//            bluePaint
//          )
//        }
//      }
//    }
  }

  private fun drawPoint(canvas: Canvas, landmark: PoseLandmark, paint: Paint){
    val point = landmark.position3D
    updatePaintColorByZValue(paint, canvas, visualizeZ, rescaleZForVisualiz, point.z, zMin, zMax)
    canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint)
  }

  private fun drawLine(canvas: Canvas, startLandmark: PoseLandmark?, endLandmark: PoseLandmark?, paint: Paint) {
    if (drawUnlikelyLines ||
        (startLandmark!!.inFrameLikelihood>MIN_IN_FRAME_LIKELIHOOD &&
         endLandmark!!.inFrameLikelihood>MIN_IN_FRAME_LIKELIHOOD)){
      val start = startLandmark!!.position3D
      val end = endLandmark!!.position3D
      // Gets average z for the current body line
      val avgZInImagePixel = (start.z + end.z) / 2
      updatePaintColorByZValue(paint, canvas, visualizeZ, rescaleZForVisualiz, avgZInImagePixel, zMin, zMax)
      canvas.drawLine(translateX(start.x), translateY(start.y), translateX(end.x), translateY(end.y), paint)
    }
  }

  companion object {
    private const val DOT_RADIUS = 8.0f
    private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
    private const val STROKE_WIDTH = 10.0f
    private const val MIN_IN_FRAME_LIKELIHOOD = 0.93f
  }
}
