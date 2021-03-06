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
package org.spout.vanilla.plugin.material.block;

import java.util.Random;

import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.range.CubicEffectRange;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.IntVector3;
import org.spout.api.math.MathHelper;
import org.spout.api.model.Model;
import org.spout.api.resource.ResourcePointer;

import org.spout.vanilla.plugin.material.VanillaBlockMaterial;

/**
 * A solid material that can spread to other materials nearby
 */
public abstract class SpreadingSolid extends Solid implements Spreading, DynamicMaterial {
	private BlockMaterial replacedMaterial;
	private static final EffectRange DEFAULT_SPREAD_RANGE = new CubicEffectRange(2);

	public SpreadingSolid(short dataMask, String name, int id, ResourcePointer<Model> model) {
		super(dataMask, name, id, model);
	}

	public SpreadingSolid(String name, int id, int data, VanillaBlockMaterial parent, ResourcePointer<Model> model) {
		super(name, id, data, parent, model);
	}

	public SpreadingSolid(String name, int id, ResourcePointer<Model> model) {
		super(name, id, model);
	}

	@Override
	public void onUpdate(BlockMaterial oldMaterial, Block block) {
		super.onUpdate(oldMaterial, block);
		block.resetDynamic();
	}

	@Override
	public boolean hasPhysics() {
		return true;
	}

	/**
	 * Sets the material replaced by this spreading solid material
	 * @param material to set to
	 * @return this spreading solid material
	 */
	public SpreadingSolid setReplacedMaterial(BlockMaterial material) {
		this.replacedMaterial = material;
		return this;
	}

	/**
	 * Gets the material replaced by this spreading solid material
	 * @return replacement material
	 */
	public BlockMaterial getReplacedMaterial() {
		return this.replacedMaterial;
	}

	/**
	 * Tests if the block can decay at the block specified<br><br>
	 * <p/>
	 * <b>Note: This should not operate on the block itself,
	 * as the block is not necessarily this material</b>
	 * @param block of this Spreading solid
	 * @return True if it can decay, False if not
	 */
	public abstract boolean canDecayAt(Block block);

	/**
	 * Tests if the block can spread from the block specified
	 * @param block of this Spreading solid
	 * @return True if it can spread, False if not
	 */
	public boolean canSpreadFrom(Block block) {
		return block.translate(BlockFace.TOP).getLight() >= this.getMinimumLightToSpread();
	}

	/**
	 * Tests if the block can spread from the block specified to a specific block
	 * @param from the block to spread
	 * @param to the block to spread
	 * @return True if it can spread, False if not
	 */
	public boolean canSpreadTo(Block from, Block to) {
		return !this.canDecayAt(to);
	}

	/**
	 * Attempts to decay this material at the block specified
	 * @param block of this material
	 * @param cause of the decay
	 */
	public boolean onDecay(Block block, Cause<?> cause) {
		return block.setMaterial(this.replacedMaterial, cause);
	}

	/**
	 * Attempts to spread this material at the block specified
	 * @param block of this material
	 * @return True if spreading was possible (but was maybe not performed), False if not
	 */
	private boolean onSpread(Block block, boolean spread) {
		Block around;
		final Random rand = MathHelper.getRandom();
		boolean couldSpread = false;
		for (IntVector3 next : this.getSpreadRange()) {
			around = block.translate(next);
			if (around.isMaterial(this.replacedMaterial)) {
				couldSpread = true;
				if (spread && rand.nextInt(4) == 0 && canSpreadTo(block, around)) {
					around.setMaterial(this);
				}
			}
		}
		return couldSpread;
	}

	/**
	 * Gets the block range within this block can spread
	 * @return spread range
	 */
	public EffectRange getSpreadRange() {
		return DEFAULT_SPREAD_RANGE;
	}

	@Override
	public EffectRange getDynamicRange() {
		return getSpreadRange();
	}

	public abstract long getSpreadingTime(Block b);

	@Override
	public void onFirstUpdate(Block b, long currentTime) {
		b.dynamicUpdate(currentTime + getSpreadingTime(b), true);
	}

	@Override
	public void onDynamicUpdate(Block block, long updateTime, int data) {
		long nextUpdate = block.getWorld().getAge() + getSpreadingTime(block);
		// Attempt to decay or spread this material
		if (canDecayAt(block)) {
			onDecay(block, this.toCause(block));
			return;
		}
		if (onSpread(block, canSpreadFrom(block))) {
			block.dynamicUpdate(nextUpdate, true);
		}
	}
}
