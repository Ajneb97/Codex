package cx.ajneb97.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cx.ajneb97.Codex;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
}
