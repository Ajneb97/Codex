package cx.ajneb97.utilidades;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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
	public static ItemStack crearSkull( String textura) {
		ItemStack item = null;
		if(UtilidadesOtros.esLegacy()) {
			item = new ItemStack(Material.valueOf("SKULL_ITEM"),1,(short)3);
		}else {
			item = new ItemStack(Material.PLAYER_HEAD);
		}
		
		if (textura.isEmpty()) return item;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", textura));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException|NoSuchFieldException|SecurityException|IllegalAccessException error) {
            error.printStackTrace();
        }
        item.setItemMeta(skullMeta);
        return item;
	}
}
