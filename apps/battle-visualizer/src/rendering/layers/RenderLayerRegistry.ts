export const RENDER_LAYERS = [
  "background",
  "grid",
  "sensors",
  "formations",
  "ships",
  "weapons",
  "effects",
  "labels",
  "selection",
  "debug",
] as const;

export type RenderLayerId = (typeof RENDER_LAYERS)[number];

export type RenderLayerDefinition = Readonly<{
  id: RenderLayerId;
  order: number;
  enabledByDefault: boolean;
}>;

export class RenderLayerRegistry {
  private readonly layers = new Map<RenderLayerId, RenderLayerDefinition>();

  public register(layer: RenderLayerDefinition): void {
    if (this.layers.has(layer.id)) {
      throw new Error(`Render layer already registered: ${layer.id}`);
    }
    this.layers.set(layer.id, layer);
  }

  public ordered(): readonly RenderLayerDefinition[] {
    return [...this.layers.values()].sort(
      (left, right) => left.order - right.order || left.id.localeCompare(right.id),
    );
  }

  public static defaults(): RenderLayerRegistry {
    const registry = new RenderLayerRegistry();
    RENDER_LAYERS.forEach((id, index) => {
      registry.register({ id, order: index * 10, enabledByDefault: id !== "debug" });
    });
    return registry;
  }
}
