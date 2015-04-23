package com.mtgames.platformer.gfx.opencl;

import com.amd.aparapi.Kernel;
import com.mtgames.utils.Debug;

public class BrightnessCL {
	public final Kernel brightnessCL;
	private boolean firstTime = true;

	public final int ALPHA = 0xea;

	public BrightnessCL(int[] pixelsCL, int[] overlayAlphaCL) {
		brightnessCL = new Kernel() {
			@Override public void run() {
				int i = getGlobalId();
				int c1Hex = pixelsCL[i];
				int c2Hex = overlayAlphaCL[i];
				if (overlayAlphaCL[i] != ALPHA) {
					overlayAlphaCL[i] = ALPHA;
				}

				if (c2Hex == 0) {
					return;
				}

				int c1Alpha = c1Hex >> 24;
				int c1Red = (c1Hex >> 16) - (c1Alpha << 8);
				int c1Green = (c1Hex >> 8) - (c1Red << 8) - (c1Alpha << 16);
				int c1Blue = (c1Hex) - (c1Red << 16) - (c1Green << 8) - (c1Alpha << 24);

				int resultRed = ((c1Red * (255 - c2Hex)) / 255);
				int resultGreen = ((c1Green * (255 - c2Hex)) / 255);
				int resultBlue = ((c1Blue * (255 - c2Hex)) / 255);

				pixelsCL[i] = ((resultRed << 16) + (resultGreen << 8) + (resultBlue));
			}
		};
	}

	public void setMode(Kernel.EXECUTION_MODE Mode) {
		brightnessCL.setExecutionMode(Mode);
	}

	public boolean running() {
		if (brightnessCL.getExecutionMode().equals(Kernel.EXECUTION_MODE.JTP)) {
			if (firstTime) {
				Debug.log("Could not run OpenCL, switching to fallback!", Debug.WARNING);
				firstTime = false;
			}
			return false;
		} else {
			return true;
		}
	}
}
