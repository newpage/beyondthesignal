export type Vector3 = Readonly<{
  x: number;
  y: number;
  z: number;
}>;

export type FleetSide = "ALLIANCE" | "HOSTILE";

export type ShipTelemetry = Readonly<{
  id: string;
  name: string;
  side: FleetSide;
  position: Vector3;
  velocity: Vector3;
  headingRadians: number;
  hull: number;
  shields: number;
  selectedTargetId?: string;
  shipClass?: string;
  maneuver?: string;
  executionState?: string;
  confidence?: number;
}>;

export type BattleFrame = Readonly<{
  battleId: string;
  tick: number;
  simulationTimeSeconds: number;
  ships: readonly ShipTelemetry[];
}>;

export type ConnectionState =
  | "CONNECTING"
  | "CONNECTED"
  | "RECONNECTING"
  | "DEMO"
  | "DISCONNECTED";

export type PlaybackState = Readonly<{
  playing: boolean;
  speed: number;
}>;

export type BattleFrameMetadataV1 = Readonly<{
  battleId: string;
  tick: number;
  simulationTimeSeconds: number;
  seed: number;
  frameVersion: "1.0";
  generatedAt: string;
  checksum: string;
}>;

export type BattleShipViewV1 = Readonly<{
  id: string;
  name: string;
  shipClass: string;
  faction: string;
  position: Vector3;
  velocity: Vector3;
  headingRadians: number;
  targetId: string | null;
  hullPercent: number;
  shieldPercent: number;
  maneuver: string;
  executionState: string;
  confidence: number;
  renderFlags: readonly string[];
}>;

export type BattleFrameV1 = Readonly<{
  metadata: BattleFrameMetadataV1;
  capabilities: Readonly<{ enabled: readonly string[] }>;
  ships: readonly BattleShipViewV1[];
  formations: readonly unknown[];
  movement: readonly unknown[];
  events: readonly unknown[];
  debug: Readonly<Record<string, string>>;
}>;
