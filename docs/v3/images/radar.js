/**
 * @schema 2.10
 * @input sweepAngle: number(min=0, max=360) = 45
 * @input sweepWidth: number = 90
 * @input sweepSlices: number(min=1) = 30
 * @input rings: number = 5
 * @input radials: number = 12
 * @input blips: number = 8
 * @input blipSize: number = 4
 * @input sweepColor: color = #00FF41
 * @input gridColor: color = #0B4D22
 * @input background: color = #041A04
 * @input gridStroke: number = 0.5
 * @input sweepStroke: number = 1.5
 */

const w = pencil.width;
const h = pencil.height;
const cx = w / 2;
const cy = h / 2;
const radius = Math.min(cx, cy) * 0.9;

const sweepAngleRad = (pencil.input.sweepAngle / 360) * Math.PI * 2 - Math.PI / 2;
const sweepWidthRad = (pencil.input.sweepWidth / 360) * Math.PI * 2;

const nodes = [];

function f(n) {
  return n.toFixed(1);
}

function toHex(n) {
  const v = Math.max(0, Math.min(255, Math.round(n))).toString(16);
  return v.length < 2 ? "0" + v : v;
}

function drawWedge(wcx, wcy, r, startAngle, endAngle, fillColor) {
  const segs = 12;
  const span = endAngle - startAngle;
  const pts = [[wcx, wcy]];
  for (let j = 0; j <= segs; j++) {
    const a = startAngle + (span * j) / segs;
    pts.push([wcx + Math.cos(a) * r, wcy + Math.sin(a) * r]);
  }

  let minX = pts[0][0];
  let minY = pts[0][1];
  let maxX = minX;
  let maxY = minY;
  for (let j = 1; j < pts.length; j++) {
    if (pts[j][0] < minX) {
      minX = pts[j][0];
    }
    if (pts[j][1] < minY) {
      minY = pts[j][1];
    }
    if (pts[j][0] > maxX) {
      maxX = pts[j][0];
    }
    if (pts[j][1] > maxY) {
      maxY = pts[j][1];
    }
  }
  let pw = maxX - minX;
  let ph = maxY - minY;
  if (pw < 0.1) {
    pw = 0.1;
  }
  if (ph < 0.1) {
    ph = 0.1;
  }

  let d = "M " + f(pts[0][0] - minX) + " " + f(pts[0][1] - minY);
  for (let j = 1; j < pts.length; j++) {
    d += " L " + f(pts[j][0] - minX) + " " + f(pts[j][1] - minY);
  }
  d += " Z";

  nodes.push({
    type: "path",
    x: minX,
    y: minY,
    width: pw,
    height: ph,
    viewBox: [0, 0, pw, ph],
    geometry: d,
    fill: [{ type: "color", color: fillColor }],
  });
}

function drawLine(x1, y1, x2, y2, thickness, color) {
  const lx = Math.min(x1, x2);
  const ly = Math.min(y1, y2);
  let lw = Math.abs(x2 - x1);
  let lh = Math.abs(y2 - y1);
  if (lw < 0.1) {
    lw = 0.1;
  }
  if (lh < 0.1) {
    lh = 0.1;
  }
  nodes.push({
    type: "path",
    x: lx,
    y: ly,
    width: lw,
    height: lh,
    viewBox: [0, 0, lw, lh],
    geometry:
      "M " + f(x1 - lx) + " " + f(y1 - ly) +
      " L " + f(x2 - lx) + " " + f(y2 - ly),
    stroke: {
      align: "center",
      thickness: thickness,
      cap: "round",
      fill: [{ type: "color", color: color }],
    },
  });
}

nodes.push({
  type: "ellipse",
  x: cx - radius - 6,
  y: cy - radius - 6,
  width: (radius + 6) * 2,
  height: (radius + 6) * 2,
  fill: [{ type: "color", color: "#000000" }],
  effect: {
    type: "shadow",
    shadowType: "outer",
    color: "#000000CC",
    offset: { x: 0, y: 4 },
    blur: 20,
  },
});

nodes.push({
  type: "ellipse",
  x: cx - radius,
  y: cy - radius,
  width: radius * 2,
  height: radius * 2,
  fill: [{ type: "color", color: pencil.input.background }],
});

const slices = Math.round(pencil.input.sweepSlices);
for (let i = 0; i < slices; i++) {
  const t = i / slices;
  const sliceStart = sweepAngleRad - sweepWidthRad * (t + 1 / slices);
  const sliceEnd = sweepAngleRad - sweepWidthRad * t;
  const alpha = Math.round(70 * Math.pow(1 - t, 1.8));
  if (alpha < 2) {
    continue;
  }
  drawWedge(cx, cy, radius - 1, sliceStart, sliceEnd, pencil.input.sweepColor + toHex(alpha));
}

for (let i = 1; i <= pencil.input.rings; i++) {
  const r = (radius / pencil.input.rings) * i;
  nodes.push({
    type: "ellipse",
    x: cx - r,
    y: cy - r,
    width: r * 2,
    height: r * 2,
    stroke: {
      align: "center",
      thickness: pencil.input.gridStroke,
      fill: [{ type: "color", color: pencil.input.gridColor }],
    },
  });
}

for (let i = 0; i < pencil.input.radials; i++) {
  const angle = (i / pencil.input.radials) * Math.PI * 2;
  drawLine(
    cx,
    cy,
    cx + Math.cos(angle) * radius,
    cy + Math.sin(angle) * radius,
    pencil.input.gridStroke,
    pencil.input.gridColor
  );
}

drawLine(
  cx,
  cy,
  cx + Math.cos(sweepAngleRad) * radius,
  cy + Math.sin(sweepAngleRad) * radius,
  0.5,
  pencil.input.sweepColor
);

for (let i = 0; i < pencil.input.blips; i++) {
  const blipAngle = Math.random() * Math.PI * 2;
  const blipDist = (Math.random() * 0.8 + 0.1) * radius;
  const bx = cx + Math.cos(blipAngle) * blipDist;
  const by = cy + Math.sin(blipAngle) * blipDist;
  const bs = pencil.input.blipSize;

  let angleDiff = sweepAngleRad - blipAngle;
  while (angleDiff < 0) {
    angleDiff += Math.PI * 2;
  }
  while (angleDiff > Math.PI * 2) {
    angleDiff -= Math.PI * 2;
  }
  const fade = Math.pow(Math.max(0, 1 - angleDiff / (Math.PI * 2)), 0.6);
  if (fade < 0.05) {
    continue;
  }

  nodes.push({
    type: "ellipse",
    x: bx - bs,
    y: by - bs,
    width: bs * 2,
    height: bs * 2,
    fill: [{ type: "color", color: pencil.input.sweepColor + toHex(Math.round(120 * fade)) }],
    effect: {
      type: "shadow",
      shadowType: "outer",
      color: pencil.input.sweepColor + toHex(Math.round(80 * fade)),
      offset: { x: 0, y: 0 },
      blur: 6,
    },
  });

  nodes.push({
    type: "ellipse",
    x: bx - bs * 0.4,
    y: by - bs * 0.4,
    width: bs * 0.8,
    height: bs * 0.8,
    fill: [{ type: "color", color: pencil.input.sweepColor + toHex(Math.round(255 * fade)) }],
  });
}

nodes.push({
  type: "ellipse",
  x: cx - 3,
  y: cy - 3,
  width: 6,
  height: 6,
  fill: [{ type: "color", color: pencil.input.sweepColor }],
  effect: {
    type: "shadow",
    shadowType: "outer",
    color: pencil.input.sweepColor + "88",
    offset: { x: 0, y: 0 },
    blur: 10,
  },
});

nodes.push({
  type: "ellipse",
  x: cx - radius,
  y: cy - radius,
  width: radius * 2,
  height: radius * 2,
  stroke: {
    align: "inside",
    thickness: 1.5,
    fill: [{ type: "color", color: pencil.input.sweepColor + "44" }],
  },
});

return nodes;
