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
package org.spout.vanilla.plugin.inventory.window.block;

import org.spout.api.entity.Player;
import org.spout.api.math.Vector2;

import org.spout.vanilla.plugin.inventory.block.DispenserInventory;
import org.spout.vanilla.plugin.inventory.util.GridInventoryConverter;
import org.spout.vanilla.plugin.inventory.window.Window;
import org.spout.vanilla.plugin.inventory.window.WindowType;

public class DispenserWindow extends Window {
	public DispenserWindow(Player owner, DispenserInventory inventory) {
		this(owner, inventory, "Dispenser");
	}

	public DispenserWindow(Player owner, DispenserInventory inventory, String title) {
		super(owner, WindowType.DISPENSER, title, 9);
		addInventoryConverter(new GridInventoryConverter(inventory, 3, Vector2.ZERO));
	}
}
