/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Vanilla is licensed under the Spout License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.vanilla.plugin.world.generator.object;

import java.util.Random;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

import org.spout.vanilla.plugin.material.block.Attachable;
import org.spout.vanilla.plugin.material.block.Directional;

public abstract class RotatableObject extends RandomObject {
	protected Quaternion rotation = Quaternion.IDENTITY;
	protected Vector3 center = new Vector3(0, 0, 0);

	public RotatableObject(Random random) {
		super(random);
	}

	protected Block getBlock(World world, int x, int y, int z) {
		return world.getBlock(applyRotation(x, y, z));
	}

	protected BlockMaterial getBlockMaterial(World world, int x, int y, int z) {
		final Vector3 rotated = applyRotation(x, y, z);
		return world.getBlockMaterial(rotated.getFloorX(), rotated.getFloorY(), rotated.getFloorZ());
	}

	protected void setBlockMaterial(World world, int x, int y, int z, BlockMaterial material, short data) {
		final Vector3 rotated = applyRotation(x, y, z);
		world.setBlockMaterial(rotated.getFloorX(), rotated.getFloorY(), rotated.getFloorZ(), material, data, null);
		if (material instanceof Directional) {
			final Directional directional = (Directional) material;
			final Block block = world.getBlock(rotated);
			final BlockFace face = directional.getFacing(block);
			if (face != BlockFace.BOTTOM && face != BlockFace.TOP) {
				directional.setFacing(block, BlockFace.fromYaw(face.getDirection().getYaw()
						+ rotation.getYaw()));
			}
		} else if (material instanceof Attachable) {
			final Attachable attachable = (Attachable) material;
			final Block block = world.getBlock(rotated);
			final BlockFace face = attachable.getAttachedFace(block);
			if (face != BlockFace.BOTTOM && face != BlockFace.TOP) {
				attachable.setAttachedFace(block, BlockFace.fromYaw(face.getDirection().getYaw()
						+ rotation.getYaw()), null);
			}
		}
	}

	private Vector3 applyRotation(int x, int y, int z) {
		return MathHelper.round(MathHelper.transform(new Vector3(x, y, z).subtract(center), rotation).add(center));
	}

	public void setCenter(Vector3 center) {
		this.center = center;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public void addRotation(Quaternion rotation) {
		this.rotation = this.rotation.multiply(rotation);
	}
}
