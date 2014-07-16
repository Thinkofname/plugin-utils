/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableMap;

import uk.co.thinkofdeath.command.CommandError;
import uk.co.thinkofdeath.command.parsers.ArgumentParser;
import uk.co.thinkofdeath.command.parsers.ParserException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parses an enchantment name into an enchantment object. This
 * class supplies mappings by default, but can be given custom mappings.
 * The keys in the map MUST be completely lowercase, otherwise behavior
 * is undefined.
 */
public class EnchantmentParser implements ArgumentParser<Enchantment> {
    private final Plugin plugin;

    private final Map<String, Enchantment> map;
    
    private static final Map<String, Enchantment> DEFAULT;
    
    static {
    	
    	ImmutableMap.Builder<String, Enchantment> map = new ImmutableMap.Builder<>();
    	
    	for(Field f : Enchantment.class.getFields()){
    		
    		if(Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && Enchantment.class.isAssignableFrom(f.getType())){
    			
    			try {
					map.put(f.getName().toLowerCase(), (Enchantment) f.get(null));
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					Bukkit.getLogger().severe("Exception building default map for " + EnchantmentParser.class.getName() + " at element " + f.getName());
					e.printStackTrace();
				}
    		}
    	}
    	
    	DEFAULT = map.build();
    }
    
    public EnchantmentParser(Plugin plugin, Map<String, Enchantment> mappings) {
        this.plugin = plugin;
        this.map = mappings;
    }

    public EnchantmentParser(Plugin plugin){
    	this(plugin, DEFAULT);
    }
    
    @Override
    public Enchantment parse(String argument) throws ParserException {
        Enchantment ench = map.get(argument.toLowerCase());
        if (ench == null) {
            throw new ParserException(new CommandError(2, "bukkit.no-ench", argument));
        }
        return ench;
    }

    @Override
    public Set<String> complete(String argument) {
        HashSet<String> completions = new HashSet<>();
        for (String name : map.keySet()) {
        	if(name.startsWith(argument.toLowerCase())){
        		completions.add(name);
        	}
        }
        return completions;
    }
}
