export type TacticalTheme = Readonly<{
  background: number;
  friendly: number;
  hostile: number;
  neutral: number;
  selection: number;
  hover: number;
  label: number;
  labelMuted: number;
  shieldHigh: number;
  shieldMedium: number;
  shieldLow: number;
  shieldCritical: number;
  hullHigh: number;
  hullMedium: number;
  hullLow: number;
  hullCritical: number;
  targetLine: number;
  beamFriendly: number;
  beamHostile: number;
  projectileFriendly: 0xffd166,
  projectileHostile: 0xff8c42,
  shieldImpact: number;
  intelligenceProjection: number;
  intelligenceConfidence: number;
  intelligenceTarget: number;
}>;

export const tacticalTheme: TacticalTheme = {
  background: 0x030713,
  friendly: 0x66d9ff,
  hostile: 0xff5d78,
  neutral: 0xaebbd0,
  selection: 0xffdf75,
  hover: 0xffffff,
  label: 0xeaf4ff,
  labelMuted: 0x8ba0bd,
  shieldHigh: 0x62d9ff,
  shieldMedium: 0x51f0d2,
  shieldLow: 0xffbd62,
  shieldCritical: 0xff6478,
  hullHigh: 0x73ef9c,
  hullMedium: 0xd4ed66,
  hullLow: 0xffa94d,
  hullCritical: 0xff596d,
  targetLine: 0x6f88a8,
  beamFriendly: 0x58dfff,
  beamHostile: 0xff496f,
  projectileFriendly: 0xffd166,
  projectileHostile: 0xff8c42,
  shieldImpact: 0x9fe9ff,
  intelligenceProjection: 0x7de5ff,
  intelligenceConfidence: 0xffd36a,
  intelligenceTarget: 0xff7f95,
};

export const statusColor = (
  value: number,
  high: number,
  medium: number,
  low: number,
  critical: number,
): number => {
  if (value >= 0.75) return high;
  if (value >= 0.5) return medium;
  if (value >= 0.25) return low;
  return critical;
};
