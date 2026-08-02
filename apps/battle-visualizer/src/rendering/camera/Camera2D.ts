export type Point2D = Readonly<{ x: number; y: number }>;

export type CameraState = Readonly<{
  position: Point2D;
  zoom: number;
}>;

export class Camera2D {
  private state: CameraState;

  public constructor(initial?: Partial<CameraState>) {
    this.state = {
      position: initial?.position ?? { x: 0, y: 0 },
      zoom: initial?.zoom ?? 1,
    };
    this.assertZoom(this.state.zoom);
  }

  public snapshot(): CameraState {
    return {
      position: { ...this.state.position },
      zoom: this.state.zoom,
    };
  }

  public panBy(delta: Point2D): CameraState {
    this.state = {
      ...this.state,
      position: {
        x: this.state.position.x + delta.x,
        y: this.state.position.y + delta.y,
      },
    };
    return this.snapshot();
  }

  public setZoom(zoom: number): CameraState {
    this.assertZoom(zoom);
    this.state = { ...this.state, zoom };
    return this.snapshot();
  }

  public zoomBy(factor: number, min = 0.25, max = 4): CameraState {
    if (!Number.isFinite(factor) || factor <= 0) {
      throw new Error("zoom factor must be positive");
    }
    return this.setZoom(Math.max(min, Math.min(max, this.state.zoom * factor)));
  }

  public worldToScreen(world: Point2D, viewport: Point2D): Point2D {
    return {
      x: viewport.x / 2 + (world.x - this.state.position.x) * this.state.zoom,
      y: viewport.y / 2 + (world.y - this.state.position.y) * this.state.zoom,
    };
  }

  public screenToWorld(screen: Point2D, viewport: Point2D): Point2D {
    return {
      x: (screen.x - viewport.x / 2) / this.state.zoom + this.state.position.x,
      y: (screen.y - viewport.y / 2) / this.state.zoom + this.state.position.y,
    };
  }

  private assertZoom(zoom: number): void {
    if (!Number.isFinite(zoom) || zoom <= 0) {
      throw new Error("zoom must be positive");
    }
  }
}
