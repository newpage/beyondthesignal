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

export type CombatVisualEvent = Readonly<{
  tick: number;
  type: string;
  message: string;
  sourceId?: string;
  targetId?: string;
  attributes: Readonly<Record<string, string>>;
}>;

export type ProjectileTelemetry = Readonly<{
  id: string;
  sourceId: string;
  targetId: string;
  weaponId: string;
  status: string;
  progress: number;
}>;

export type WreckTelemetry = Readonly<{
  id: string;
  formerShipId: string;
  faction: string;
  destroyedTick: number;
  cause: string;
}>;

export type FleetTelemetry = Readonly<{ id: string; name: string; commander: string; faction: string; doctrine: string; squadronIds: readonly string[]; orderIds: readonly string[]; }>;
export type SquadronTelemetry = Readonly<{ id: string; name: string; leaderId: string; memberIds: readonly string[]; formation: string; morale: number; status: string; }>;
export type ThreatTelemetry = Readonly<{ participantId: string; score: number; firepower: number; hullRatio: number; }>;
export type FleetDecisionTelemetry = Readonly<{ fleetId: string; doctrine: string; objective: string; primaryTargetId?: string; threats: readonly ThreatTelemetry[]; commanderStatus: string; retreat: boolean; generatedTick: number; }>;

export type BattleFrame = Readonly<{
  battleId: string;
  tick: number;
  simulationTimeSeconds: number;
  ships: readonly ShipTelemetry[];
  projectiles?: readonly ProjectileTelemetry[];
  wrecks?: readonly WreckTelemetry[];
  fleets?: readonly FleetTelemetry[];
  squadrons?: readonly SquadronTelemetry[];
  fleetDecisions?: readonly FleetDecisionTelemetry[];
  events?: readonly CombatVisualEvent[];
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

export type ReplayMarkerType =
  | "BATTLE_START"
  | "TARGET_ACQUIRED"
  | "TARGET_CHANGED"
  | "SHIP_CRITICAL"
  | "SHIP_DESTROYED"
  | "BOOKMARK";

export type ReplayMarker = Readonly<{
  tick: number;
  type: ReplayMarkerType;
  label: string;
  shipId?: string;
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

export type BattleEventViewV1 = Readonly<{
  tick: number;
  type: string;
  message: string;
  attributes: Readonly<Record<string, string>>;
}>;

export type BattleProjectileViewV1 = Readonly<{
  id: string;
  sourceId: string;
  targetId: string;
  weaponId: string;
  status: string;
  progress: number;
}>;

export type BattleWreckViewV1 = Readonly<{
  id: string;
  formerShipId: string;
  faction: string;
  destroyedTick: number;
  cause: string;
}>;

export type BattleFleetViewV1 = Readonly<{ id: string; name: string; commander: string; faction: string; doctrine: string; squadronIds: readonly string[]; orderIds: readonly string[]; }>;
export type BattleSquadronViewV1 = Readonly<{ id: string; name: string; leaderId: string; memberIds: readonly string[]; formation: string; currentOrderId: string | null; priorityTargetId: string | null; morale: number; status: string; }>;
export type BattleFleetDecisionViewV1 = Readonly<{ fleetId: string; doctrine: string; objective: string; primaryTargetId: string | null; threats: readonly ThreatTelemetry[]; commanderStatus: string; retreat: boolean; generatedTick: number; }>;

export type BattleFrameV1 = Readonly<{
  metadata: BattleFrameMetadataV1;
  capabilities: Readonly<{ enabled: readonly string[] }>;
  ships: readonly BattleShipViewV1[];
  formations: readonly unknown[];
  movement: readonly unknown[];
  projectiles?: readonly BattleProjectileViewV1[];
  wrecks?: readonly BattleWreckViewV1[];
  fleets?: readonly BattleFleetViewV1[];
  squadrons?: readonly BattleSquadronViewV1[];
  fleetDecisions?: readonly BattleFleetDecisionViewV1[];
  events: readonly BattleEventViewV1[];
  debug: Readonly<Record<string, string>>;
}>;
