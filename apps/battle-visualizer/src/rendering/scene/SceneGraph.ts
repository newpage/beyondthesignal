import type { FleetSide, Vector3 } from "../../types";

export type SceneNodeType = "SHIP" | "ENGAGEMENT";

export type SceneNode = Readonly<{
  id: string;
  type: SceneNodeType;
  visible: boolean;
  worldPosition: Vector3;
}>;

export type ShipSceneNode = SceneNode & Readonly<{
  type: "SHIP";
  name: string;
  side: FleetSide;
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

export type EngagementSceneNode = SceneNode & Readonly<{
  type: "ENGAGEMENT";
  sourceShipId: string;
  targetShipId: string;
  sourcePosition: Vector3;
  targetPosition: Vector3;
  side: FleetSide;
  pulsePhase: number;
  firing: boolean;
}>;

export class SceneGraph {
  private readonly nodes = new Map<string, SceneNode>();

  public upsert(node: SceneNode): void {
    this.nodes.set(node.id, node);
  }

  public remove(id: string): boolean {
    return this.nodes.delete(id);
  }

  public get<T extends SceneNode = SceneNode>(id: string): T | undefined {
    return this.nodes.get(id) as T | undefined;
  }

  public all<T extends SceneNode = SceneNode>(): readonly T[] {
    return [...this.nodes.values()] as T[];
  }

  public byType<T extends SceneNode = SceneNode>(type: SceneNodeType): readonly T[] {
    return this.all<T>().filter((node) => node.type === type);
  }

  public clear(): void {
    this.nodes.clear();
  }
}
