package com.mtgames.platformer.gfx.lighting;

import com.amd.aparapi.Kernel;
import com.mtgames.platformer.debug.Debug;

public class OpenCL {
	public final Kernel alphaBlendCL;
	private boolean firstTime = true;

	public final int ALPHA = 0xea;

	public OpenCL(int[] pixelsCL, long[] overlayAlphaCL) {
		alphaBlendCL = new Kernel() {
			@Override public void run() {
				int i = getGlobalId();
				int c1Hex = pixelsCL[i];
				long c2Hex = overlayAlphaCL[i] << 24;
				if (overlayAlphaCL[i] != ALPHA) {
					overlayAlphaCL[i] = ALPHA;
				}

				if (c2Hex >> 24 == 0) {
					return;
				}

				int c1Alpha = c1Hex >> 24;
				long c2Alpha = c2Hex >> 24;

				int c1Red = (c1Hex >> 16) - (c1Alpha << 8);
				long c2Red = ((c2Hex >> 16) - (c2Alpha << 8));

				int c1Green = (c1Hex >> 8) - (c1Red << 8) - (c1Alpha << 16);
				long c2Green = ((c2Hex >> 8) - (c2Red << 8) - (c2Alpha << 16));

				int c1Blue = (c1Hex) - (c1Red << 16) - (c1Green << 8) - (c1Alpha << 24);
				long c2Blue = ((c2Hex) - (c2Red << 16) - (c2Green << 8) - (c2Alpha << 24));

				long resultRed = ((c2Red * c2Alpha + c1Red * (255 - c2Alpha)) / 255);
				long resultGreen = ((c2Green * c2Alpha + c1Green * (255 - c2Alpha)) / 255);
				long resultBlue = ((c2Blue * c2Alpha + c1Blue * (255 - c2Alpha)) / 255);

				pixelsCL[i] = (int) ((resultRed << 16) + (resultGreen << 8) + (resultBlue));
			}
		};
	}

	public void setMode(Kernel.EXECUTION_MODE Mode) {
		alphaBlendCL.setExecutionMode(Mode);
	}

	public boolean running() {
		if (alphaBlendCL.getExecutionMode().equals(Kernel.EXECUTION_MODE.JTP)) {
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
