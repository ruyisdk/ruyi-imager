// Copyright (c) 2026 Glavo
// SPDX-License-Identifier: MPL-2.0

package org.glavo.ruyi.imager.core.image;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Immutable image catalog snapshot.
///
/// @param images available images.
@NotNullByDefault
public record ImageCatalog(@Unmodifiable List<ImageEntry> images) {
    /// Copies catalog content into an immutable list.
    public ImageCatalog {
        images = List.copyOf(images);
    }

    /// Lists combos and standalone packages, without repeating a combo's exact component versions.
    public @Unmodifiable List<ImageEntry> browsableImages() {
        var components = new HashMap<String, Set<String>>();
        for (ImageEntry image : images) {
            if ("image-combo".equals(image.category())) {
                var atoms = components.computeIfAbsent(image.repoId(), _ -> new HashSet<>());
                image.components().forEach(component -> atoms.add(component.atom()));
            }
        }
        return images.stream().filter(image -> "image-combo".equals(image.category())
                || !components.getOrDefault(image.repoId(), Set.of()).contains(image.atom())).toList();
    }
}
