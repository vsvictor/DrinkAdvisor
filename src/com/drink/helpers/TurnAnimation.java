/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drink.helpers;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

public class TurnAnimation extends Animation 
{
    private final float mAngle;
    private final float mCenterX;
    private float mOffsetX;
    private float mOffsetZ;
    private final boolean mReverse;
    private final boolean mOn;
    private Camera mCamera;
    private final float mScreenHeight;
    private final float mScale = 1.0f;

    public TurnAnimation(float angle, float centerX, float offsetX, float offsetZ, boolean reverse, boolean on) 
    {
    	float w = (float) CommonHelper.getScreenWidth();
    	mScreenHeight = (float) CommonHelper.getScreenHeight();
    	
        mAngle = angle;
        if (reverse)
        {
        	mCenterX = (1.0f - centerX) * w;
        }
        else
        {
        	mCenterX = centerX * w;
        }
        
  		mOffsetX = offsetX * w;
  		mOffsetZ = offsetZ * w;
        mReverse = reverse;
        mOn = on;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) 
    {
        super.initialize(width, height, parentWidth, parentHeight);
        
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) 
    {
    	if (!mOn)
    	{
    		interpolatedTime = 1.0f - interpolatedTime;
    	}
    	
        final Matrix matrix = t.getMatrix();

    	mCamera.save();
    	if (mReverse)
    	{
    		mCamera.rotateY(-mAngle * interpolatedTime);
        	mCamera.translate(mOffsetX * interpolatedTime, 0.0f, mOffsetZ * interpolatedTime);
    	}
    	else
    	{
    		mCamera.rotateY(mAngle * interpolatedTime);
        	mCamera.translate(-mOffsetX * interpolatedTime, 0.0f, -mOffsetZ * interpolatedTime);
    	}
        mCamera.getMatrix(matrix);
        mCamera.restore();

        matrix.preTranslate(-mCenterX, -mScreenHeight / 2.0f);
        matrix.postTranslate(mCenterX, mScreenHeight / 2.0f);
    }
}