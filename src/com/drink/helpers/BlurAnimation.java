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

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class BlurAnimation extends Animation 
{
	private final View mView;
    private final float mScale;
    private final boolean mOn;

    public BlurAnimation(View view, float scale, boolean on)
    {
    	mView = view;
        mOn = on;
        mScale = scale;
        
        if (on)
        {
            mView.setAlpha(0.0f);
            mView.setScaleX(scale);
            mView.setScaleY(scale);
        }
        else
        {
        	mView.setAlpha(1.0f);
        	mView.setScaleX(1.0f);
        	mView.setScaleY(1.0f);
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) 
    {
    	if (!mOn)
    	{
    		interpolatedTime = 1.0f - interpolatedTime;
    	}
    		
    	float scale = (1.0f - interpolatedTime) * (mScale - 1.0f) + 1.0f;
    	
    	mView.setScaleX(scale);
    	mView.setScaleY(scale);
    	
    	mView.setAlpha(interpolatedTime);
    }
}