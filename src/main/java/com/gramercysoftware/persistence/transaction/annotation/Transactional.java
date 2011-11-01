/*
 * Transactional annotation
 * Copyright (C) 2011 David Harcombe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gramercysoftware.persistence.transaction.annotation;

/**
 * Any method with this annotation will be executed under a db transaction.
 * Nesting is supported i.e a method with this annotation can call another one
 * with the same annotation in which case the top level method will be the one
 * controlling transactional boundary.
 */
public @interface Transactional {
}
