import AppKit
import Foundation

let arguments = CommandLine.arguments
guard arguments.count == 2 else {
    fputs("사용법: swift scripts/normalize-action-button-atlas.swift <atlas.png>\n", stderr)
    exit(2)
}

let atlasURL = URL(fileURLWithPath: arguments[1])
guard let source = NSImage(contentsOf: atlasURL),
      let sourceRepresentation = source.representations.first,
      sourceRepresentation.pixelsWide == 1_254,
      sourceRepresentation.pixelsHigh == 1_254 else {
    fputs("1254 × 1254 PNG 아틀라스만 정규화할 수 있습니다.\n", stderr)
    exit(3)
}

let cell = 418
let inset = 21
let content = cell - inset * 2
guard let output = NSBitmapImageRep(
    bitmapDataPlanes: nil,
    pixelsWide: cell * 3,
    pixelsHigh: cell * 3,
    bitsPerSample: 8,
    samplesPerPixel: 4,
    hasAlpha: true,
    isPlanar: false,
    colorSpaceName: .deviceRGB,
    bytesPerRow: 0,
    bitsPerPixel: 0
) else {
    fputs("출력 비트맵을 만들 수 없습니다.\n", stderr)
    exit(4)
}

NSGraphicsContext.saveGraphicsState()
guard let context = NSGraphicsContext(bitmapImageRep: output) else {
    fputs("출력 그래픽 컨텍스트를 만들 수 없습니다.\n", stderr)
    exit(5)
}
NSGraphicsContext.current = context
NSColor.clear.setFill()
NSRect(x: 0, y: 0, width: cell * 3, height: cell * 3).fill()
context.imageInterpolation = .high

for row in 0..<3 {
    for column in 0..<3 {
        let sourceRect = NSRect(
            x: column * cell,
            y: (2 - row) * cell,
            width: cell,
            height: cell
        )
        let destinationRect = NSRect(
            x: column * cell + inset,
            y: (2 - row) * cell + inset,
            width: content,
            height: content
        )
        source.draw(
            in: destinationRect,
            from: sourceRect,
            operation: .sourceOver,
            fraction: 1,
            respectFlipped: true,
            hints: [.interpolation: NSImageInterpolation.high]
        )
    }
}
context.flushGraphics()
NSGraphicsContext.restoreGraphicsState()

guard let png = output.representation(using: .png, properties: [:]) else {
    fputs("PNG 인코딩에 실패했습니다.\n", stderr)
    exit(6)
}
try png.write(to: atlasURL, options: .atomic)
