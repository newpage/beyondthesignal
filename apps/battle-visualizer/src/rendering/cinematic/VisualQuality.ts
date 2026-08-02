export const VISUAL_QUALITIES = ["LOW", "MEDIUM", "HIGH", "ULTRA"] as const;

export type VisualQuality = typeof VISUAL_QUALITIES[number];

export type VisualQualityProfile = Readonly<{
  particleCount: number;
  sensorSweep: boolean;
  shieldRipples: boolean;
  cameraShake: boolean;
}>;

export const visualQualityProfile = (
  quality: VisualQuality,
): VisualQualityProfile => {
  switch (quality) {
    case "LOW":
      return { particleCount: 0, sensorSweep: false, shieldRipples: false, cameraShake: false };
    case "MEDIUM":
      return { particleCount: 3, sensorSweep: true, shieldRipples: true, cameraShake: false };
    case "HIGH":
      return { particleCount: 6, sensorSweep: true, shieldRipples: true, cameraShake: true };
    case "ULTRA":
      return { particleCount: 10, sensorSweep: true, shieldRipples: true, cameraShake: true };
  }
};
