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

  public setPosition(position: Point2D): CameraState {
    this.state = {
      ...this.state,
      position: { ...position },
    };
    return this.snapshot();
  }

  public reset(): CameraState {
    this.state = { position: { x: 0, y: 0 }, zoom: 1 };
    return this.snapshot();
  }

  public fit(
    points: readonly Point2D[],
    viewport: Point2D,
    padding = 120,
  ): CameraState {
    if (points.length === 0) return this.reset();
    const xs = points.map((point) => point.x);
    const ys = points.map((point) => point.y);
    const minX = Math.min(...xs);
    const maxX = Math.max(...xs);
    const minY = Math.min(...ys);
    const maxY = Math.max(...ys);
    const width = Math.max(1, maxX - minX);
    const height = Math.max(1, maxY - minY);
    const availableWidth = Math.max(1, viewport.x - padding * 2);
    const availableHeight = Math.max(1, viewport.y - padding * 2);
    const zoom = Math.max(0.25, Math.min(4, Math.min(
      availableWidth / width,
      availableHeight / height,
    )));
    this.state = {
      position: { x: (minX + maxX) / 2, y: (minY + maxY) / 2 },
      zoom,
    };
    return this.snapshot();
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
