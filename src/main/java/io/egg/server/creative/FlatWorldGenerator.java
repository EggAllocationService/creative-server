package io.egg.server.creative;

import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class FlatWorldGenerator implements ChunkGenerator {
    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        for (int x = 0; x <= 16; x++) {
            for (int z = 0; z <= 16; z++) {
                for (int y = 1; y <= 64; y++) {
                    if (y == 64) {
                        // layer of grass on top
                        batch.setBlock(x, y, z, Block.GRASS_BLOCK);
                    } else {
                        // main body
                        batch.setBlock(x, y, z, Block.STONE);
                    }
                }
            }
        }
    }

    @Override
    public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, Biome.PLAINS);
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return null;
    }
}
