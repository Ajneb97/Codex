package cx.ajneb97.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cx.ajneb97.Codex;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class UtilidadesItems {

	@SuppressWarnings("deprecation")
	public static ItemStack crearItem(String id) {
		String[] idsplit = new String[2];
		int DataValue = 0;
		ItemStack stack = null;
		if(id.contains(":")){
			  idsplit = id.split(":");
			  String stringDataValue = idsplit[1];
			  DataValue = Integer.valueOf(stringDataValue);
			  Material mat = Material.getMaterial(idsplit[0].toUpperCase()); 
			  stack = new ItemStack(mat,1,(short)DataValue);	             	  
		}else{
			  Material mat = Material.getMaterial(id.toUpperCase());
			  stack = new ItemStack(mat,1);	  			  
		}
		return stack;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack crearSkull(String textura) {
		ItemStack item = null;
		if(UtilidadesOtros.esLegacy()) {
			item = new ItemStack(Material.valueOf("SKULL_ITEM"),1,(short)3);
		}else {
			item = new ItemStack(Material.PLAYER_HEAD);
		}
		
		if (textura.isEmpty()) return item;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();


		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)){
			UUID uuid = UUID.randomUUID();
			PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
			PlayerTextures textures = profile.getTextures();
			URL url;
			try {
				String decoded = new String(Base64.getDecoder().decode(textura));
				String decodedFormatted = decoded.replaceAll("\\s", "");
				JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
				String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN")
						.getAsJsonObject().get("url").getAsString();

				url = new URL(urlText);
			} catch (Exception error) {
				error.printStackTrace();
				return null;
			}
			textures.setSkin(url);
			profile.setTextures(textures);
			skullMeta.setOwnerProfile(profile);
		}else{
			GameProfile profile = new GameProfile(UUID.randomUUID(), "");
			profile.getProperties().put("textures", new Property("textures", textura));

			try {
				Field profileField = skullMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(skullMeta, profile);
			} catch (IllegalArgumentException|NoSuchFieldException|SecurityException|IllegalAccessException error) {
				error.printStackTrace();
			}
		}
		item.setItemMeta(skullMeta);

        return item;
	}

	public static void setCustomModelComponentData(ItemStack item, FileConfiguration config, String path, ItemMeta meta){
		List<String> cFlags = new ArrayList<>();
		List<String> cFloats = new ArrayList<>();
		List<String> cColors = new ArrayList<>();
		List<String> cStrings = new ArrayList<>();
		if(config.contains(path+".flags")) {
			cFlags = config.getStringList(path+".flags");
		}
		if(config.contains(path+".floats")) {
			cFloats = config.getStringList(path+".floats");
		}
		if(config.contains(path+".colors")) {
			cColors = config.getStringList(path+".colors");
		}
		if(config.contains(path+".strings")) {
			cStrings = config.getStringList(path+".strings");
		}

		CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
		customModelDataComponent.setFlags(cFlags.stream()
				.map(Boolean::parseBoolean)
				.collect(Collectors.toList()));
		customModelDataComponent.setFloats(cFloats.stream()
				.map(Float::parseFloat)
				.collect(Collectors.toList()));
		customModelDataComponent.setColors(cColors.stream()
				.map(rgb -> Color.fromRGB(Integer.parseInt(rgb)))
				.collect(Collectors.toList()));
		customModelDataComponent.setStrings(new ArrayList<>(cStrings));
		meta.setCustomModelDataComponent(customModelDataComponent);
	}
}
