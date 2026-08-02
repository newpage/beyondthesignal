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
