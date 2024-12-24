package cx.ajneb97.utils;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import cx.ajneb97.Codex;
import cx.ajneb97.model.item.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ItemUtils {


	@SuppressWarnings("deprecation")
	public static ItemStack createItemFromID(String id) {
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

	public static ItemStack setTagStringItem(Codex plugin, ItemStack item, String key, String value) {
		return plugin.getNmsManager().setTagStringItem(item,key,value);
	}

	public static String getTagStringItem(Codex plugin, ItemStack item, String key) {
		if(item == null || item.getType().equals(Material.AIR)){
			return null;
		}
		return plugin.getNmsManager().getTagStringItem(item,key);
	}

	public static ItemStack removeTagItem(Codex plugin, ItemStack item, String key) {
		return plugin.getNmsManager().removeTagItem(item,key);
	}

	public static List<String> getNBT(Codex plugin, ItemStack item){
		return plugin.getNmsManager().getNBT(item);
	}

	public static ItemStack setNBT(Codex plugin, ItemStack item, List<String> nbtList){
		return plugin.getNmsManager().setNBT(item,nbtList);
	}

	public static CommonItemSkullData getSkullData(ItemStack item) {
		CommonItemSkullData kitItemSkullData = null;
		String owner = null;
		String texture = null;
		String id = null;

		String typeName = item.getType().name();
		if(!typeName.equals("PLAYER_HEAD") && !typeName.equals("SKULL_ITEM")) {
			return null;
		}

		SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R1)){
			PlayerProfile profile = skullMeta.getOwnerProfile();
			if(profile != null){
				owner = profile.getName();
				if(profile.getUniqueId() != null){
					id = profile.getUniqueId().toString();
				}
				if(profile.getTextures() != null){
					PlayerTextures textures = profile.getTextures();
					if(textures != null){
						JsonObject skinJsonObject = new JsonObject();
						skinJsonObject.addProperty("url", textures.getSkin().toString());
						JsonObject texturesJsonObject = new JsonObject();
						texturesJsonObject.add("SKIN", skinJsonObject);
						JsonObject minecraftTexturesJsonObject = new JsonObject();
						minecraftTexturesJsonObject.add("textures", texturesJsonObject);
						texture = new String(Base64.getEncoder().encode(minecraftTexturesJsonObject.toString().getBytes()));
					}
				}
			}
		}else{
			Field profileField;
			try {
				profileField = skullMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);

				GameProfile gameProfile = (GameProfile) profileField.get(skullMeta);
				if(gameProfile != null && gameProfile.getProperties() != null) {
					PropertyMap propertyMap = gameProfile.getProperties();
					owner = gameProfile.getName();
					if(gameProfile.getId() != null) {
						id = gameProfile.getId().toString();
					}

					for(Property p : propertyMap.values()) {
						if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)){
							String pName = (String)p.getClass().getMethod("name").invoke(p);
							if(pName.equals("textures")){
								texture = (String)p.getClass().getMethod("value").invoke(p);
							}
						}else{
							if(p.getName().equals("textures")) {
								texture = p.getValue();
							}
						}
					}
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
					 | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		if(texture != null || id != null || owner != null) {
			kitItemSkullData = new CommonItemSkullData(owner,texture,id);
		}

		return kitItemSkullData;
	}

	@SuppressWarnings("deprecation")
	public static void setSkullData(ItemStack item, CommonItemSkullData skullData, Player player){
		String typeName = item.getType().name();
		if(!typeName.equals("PLAYER_HEAD") && !typeName.equals("SKULL_ITEM")) {
			return;
		}

		if(skullData == null) {
			return;
		}

		String texture = skullData.getTexture();
		String owner = skullData.getOwner();
		if(owner != null && player != null) {
			owner = owner.replace("%player%", player.getName());
		}
		String id = skullData.getId();
		SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
		if(texture == null && owner != null) {
			skullMeta.setOwner(owner);
			item.setItemMeta(skullMeta);
			return;
		}

		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)){
			UUID uuid = id != null ? UUID.fromString(id) : UUID.randomUUID();
			PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
			PlayerTextures textures = profile.getTextures();
			URL url;
			try {
				String decoded = new String(Base64.getDecoder().decode(texture));
				String decodedFormatted = decoded.replaceAll("\\s", "");
				JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
				String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN")
						.getAsJsonObject().get("url").getAsString();

				url = new URL(urlText);
			} catch (Exception error) {
				error.printStackTrace();
				return;
			}
			textures.setSkin(url);
			profile.setTextures(textures);
			skullMeta.setOwnerProfile(profile);
		}else{
			GameProfile profile = null;
			if(id == null) {
				profile = new GameProfile(UUID.randomUUID(), owner != null ? owner : "");
			}else {
				profile = new GameProfile(UUID.fromString(id), owner != null ? owner : "");
			}
			profile.getProperties().put("textures", new Property("textures", texture));

			try {
				Field profileField = skullMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(skullMeta, profile);
			} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
				error.printStackTrace();
			}
		}

		item.setItemMeta(skullMeta);
	}

	public static CommonItemPotionData getPotionData(ItemStack item) {
		CommonItemPotionData potionData = null;
		List<String> potionEffectsList = new ArrayList<String>();
		boolean upgraded = false;
		boolean extended = false;
		String potionType = null;
		int potionColor = 0;

		String typeName = item.getType().name();
		if(!typeName.contains("POTION") && !typeName.equals("TIPPED_ARROW")) {
			return null;
		}

		if(!(item.getItemMeta() instanceof PotionMeta)) {
			return null;
		}

		PotionMeta meta = (PotionMeta) item.getItemMeta();
		if(meta.hasCustomEffects()) {
			List<PotionEffect> potionEffects = meta.getCustomEffects();
			for(int i=0;i<potionEffects.size();i++) {
				String type = potionEffects.get(i).getType().getName();
				int amplifier = potionEffects.get(i).getAmplifier();
				int duration = potionEffects.get(i).getDuration();
				potionEffectsList.add(type+";"+amplifier+";"+duration);
			}
		}
		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_9_R1)) {
			if(meta.hasColor()) {
				potionColor = meta.getColor().asRGB();
			}

			if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)) {
				if(meta.getBasePotionType() != null){
					potionType = meta.getBasePotionType().name();
				}
			}else{
				PotionData basePotionData = meta.getBasePotionData();
				extended = basePotionData.isExtended();
				upgraded = basePotionData.isUpgraded();
				potionType = basePotionData.getType().name();
			}
		}

		potionData = new CommonItemPotionData(upgraded,extended,potionType,potionColor,potionEffectsList);

		return potionData;
	}

	public static void setPotionData(ItemStack item,CommonItemPotionData potionData){
		String typeName = item.getType().name();
		if(!typeName.contains("POTION") && !typeName.equals("TIPPED_ARROW")) {
			return;
		}

		if(potionData == null) {
			return;
		}

		if(!(item.getItemMeta() instanceof PotionMeta)) {
			return;
		}

		PotionMeta meta = (PotionMeta) item.getItemMeta();

		List<String> potionEffects = potionData.getPotionEffects();
		if(potionEffects != null) {
			for(int i=0;i<potionEffects.size();i++) {
				String[] sep = potionEffects.get(i).split(";");
				String type = sep[0];
				int amplifier = Integer.valueOf(sep[1]);
				int duration = Integer.valueOf(sep[2]);
				meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(type),duration,amplifier), false);
			}
		}

		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_9_R1)) {
			int color = potionData.getPotionColor();
			if(color != 0) {
				meta.setColor(Color.fromRGB(color));
			}
			if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)) {
				if(potionData.getPotionType() != null && !potionData.getPotionType().isEmpty()){
					meta.setBasePotionType(PotionType.valueOf(potionData.getPotionType()));
				}
			}else{
				PotionData basePotionData = new PotionData(
						PotionType.valueOf(potionData.getPotionType()),
						potionData.isExtended(),
						potionData.isUpgraded());

				meta.setBasePotionData(basePotionData);
			}
		}

		item.setItemMeta(meta);
	}

	public static CommonItemBannerData getBannerData(ItemStack item) {
		CommonItemBannerData bannerData = null;
		List<String> bannerPatterns = new ArrayList<String>();
		String baseColor = null;

		List<Pattern> patterns = new ArrayList<Pattern>();

		String typeName = item.getType().name();
		if(typeName.contains("BANNER") || typeName.contains("PATTERN")) {
			if(item.getItemMeta() instanceof BannerMeta) {
				BannerMeta meta = (BannerMeta) item.getItemMeta();
				patterns = meta.getPatterns();
			}
		}else if(typeName.equals("SHIELD")) {
			BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
			if(meta.hasBlockState()) {
				Banner banner = (Banner) meta.getBlockState();
				patterns = banner.getPatterns();
				if(OtherUtils.isLegacy()) {
					baseColor = banner.getBaseColor().name();
				}else {
					baseColor = banner.getType().name();
				}
			}
		}else {
			return null;
		}

		for(Pattern p : patterns) {
			bannerPatterns.add(p.getColor().name()+";"+getBannerPatternName(p));
		}

		if(bannerPatterns.isEmpty() && baseColor == null) {
			return null;
		}

		bannerData = new CommonItemBannerData(bannerPatterns,baseColor);
		return bannerData;
	}

	public static void setBannerData(ItemStack item,CommonItemBannerData bannerData){
		String typeName = item.getType().name();

		if(bannerData == null) {
			return;
		}

		List<String> bannerPatterns = bannerData.getPatterns();
		String baseColor = bannerData.getBaseColor();

		if(typeName.contains("BANNER") || typeName.contains("PATTERN")) {
			BannerMeta meta = (BannerMeta) item.getItemMeta();
			if(bannerPatterns != null) {
				for(String pattern : bannerPatterns) {
					String[] patternSplit = pattern.split(";");
					String patternColor = patternSplit[0];
					String patternName = patternSplit[1];
					meta.addPattern(new Pattern(DyeColor.valueOf(patternColor),getBannerPatternByName(patternName)));
				}
				item.setItemMeta(meta);
			}
		}else if(typeName.equals("SHIELD")) {
			BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
			Banner banner = (Banner) meta.getBlockState();
			if(OtherUtils.isLegacy()) {
				banner.setBaseColor(DyeColor.valueOf(baseColor));
			}else {
				String fixedColor = baseColor.replace("_BANNER", "");
				banner.setBaseColor(DyeColor.valueOf(fixedColor));
			}
			if(bannerPatterns != null) {
				for(String pattern : bannerPatterns) {
					String[] patternSplit = pattern.split(";");
					String patternColor = patternSplit[0];
					String patternName = patternSplit[1];
					banner.addPattern(new Pattern(DyeColor.valueOf(patternColor),getBannerPatternByName(patternName)));
				}
			}
			banner.update();
			meta.setBlockState(banner);
			item.setItemMeta(meta);
		}
	}

	public static CommonItemFireworkData getFireworkData(ItemStack item) {
		CommonItemFireworkData fireworkData = null;

		List<String> fireworkRocketEffects = new ArrayList<String>();
		String fireworkStarEffect = null;
		int fireworkPower = 0;

		String typeName = item.getType().name();
		boolean isFireworkCharge = false;
		List<FireworkEffect> effects = new ArrayList<FireworkEffect>();

		if(typeName.equals("FIREWORK") || typeName.equals("FIREWORK_ROCKET")) {
			FireworkMeta meta = (FireworkMeta) item.getItemMeta();
			fireworkPower = meta.getPower();
			effects = meta.getEffects();
		}else if(typeName.equals("FIREWORK_STAR") || typeName.equals("FIREWORK_CHARGE")) {
			FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
			FireworkEffect effect = meta.getEffect();
			effects.add(effect);
			isFireworkCharge = true;
		}else {
			return null;
		}

		for(FireworkEffect e : effects) {
			if(e == null) {
				continue;
			}
			String line = e.getType().name()+";";
			List<Color> colors = e.getColors();
			String colorsLine = "";
			for(int i=0;i<colors.size();i++) {
				if(colors.size() <= (i+1)) {
					colorsLine = colorsLine+colors.get(i).asRGB()+";";
				}else {
					colorsLine = colorsLine+colors.get(i).asRGB()+",";
				}
			}
			List<Color> fadeColors = e.getFadeColors();
			String fadeColorsLine = "";
			for(int i=0;i<fadeColors.size();i++) {
				if(fadeColors.size() <= (i+1)) {
					fadeColorsLine = fadeColorsLine+fadeColors.get(i).asRGB();
				}else {
					fadeColorsLine = fadeColorsLine+fadeColors.get(i).asRGB()+",";
				}
			}
			line = line+colorsLine+fadeColorsLine+";"+e.hasFlicker()+";"+e.hasTrail();

			if(isFireworkCharge) {
				fireworkStarEffect = line;
				break;
			}

			fireworkRocketEffects.add(line);
		}

		fireworkData = new CommonItemFireworkData(fireworkRocketEffects,fireworkStarEffect,fireworkPower);

		return fireworkData;
	}

	public static void setFireworkData(ItemStack item,CommonItemFireworkData fireworkData){
		String typeName = item.getType().name();

		if(fireworkData == null) {
			return;
		}

		int power = fireworkData.getFireworkPower();
		List<String> rocketEffects = fireworkData.getFireworkRocketEffects();
		String starEffect = fireworkData.getFireworkStarEffect();

		if(typeName.equals("FIREWORK") || typeName.equals("FIREWORK_ROCKET")) {
			FireworkMeta meta = (FireworkMeta) item.getItemMeta();
			if(rocketEffects != null) {
				for(int i=0;i<rocketEffects.size();i++) {
					FireworkEffect effect = getFireworkEffect(rocketEffects.get(i));
					meta.addEffect(effect);
				}
			}
			meta.setPower(power);
			item.setItemMeta(meta);
		}else if(typeName.equals("FIREWORK_STAR") || typeName.equals("FIREWORK_CHARGE")) {
			FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
			if(starEffect != null) {
				meta.setEffect(getFireworkEffect(starEffect));
			}
			item.setItemMeta(meta);
		}
	}

	private static FireworkEffect getFireworkEffect(String line) {
		String[] sep = line.split(";");
		String type = sep[0];
		String[] colors = sep[1].split(",");
		List<Color> colorsList = new ArrayList<Color>();
		for(int c=0;c<colors.length;c++) {
			colorsList.add(Color.fromRGB(Integer.valueOf(colors[c])));
		}
		List<Color> fadeColorsList = new ArrayList<Color>();
		if(!sep[2].equals("")) {
			String[] fadeColors = sep[2].split(",");
			for(int c=0;c<fadeColors.length;c++) {
				fadeColorsList.add(Color.fromRGB(Integer.valueOf(fadeColors[c])));
			}
		}

		boolean flicker = Boolean.valueOf(sep[3]);
		boolean trail = Boolean.valueOf(sep[4]);
		return FireworkEffect.builder().flicker(flicker).trail(trail).with(Type.valueOf(type))
				.withColor(colorsList).withFade(fadeColorsList).build();
	}

	public static List<String> getAttributes(Codex plugin, ItemStack item){
		if(OtherUtils.isLegacy()) {
			return plugin.getNmsManager().getAttributes(item);
		}else {
			//1.13+
			ServerVersion serverVersion = Codex.serverVersion;
			boolean newSystem = serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R1);

			ItemMeta meta = item.getItemMeta();
			if(meta.hasAttributeModifiers()) {
				Multimap<Attribute,AttributeModifier> attributes = meta.getAttributeModifiers();
				Set<Attribute> set = attributes.keySet();

				List<String> attributeList = new ArrayList<String>();
				for(Attribute a : set) {
					Collection<AttributeModifier> listModifiers = attributes.get(a);
					for(AttributeModifier m : listModifiers) {
						String line;
						if(newSystem){
							line = getAttributeName(a)+";"+m.getOperation().name()+";"+m.getAmount()+";"+m.getKey().getNamespace()+":"+m.getKey().getKey();
							line=line+";"+m.getSlotGroup().toString();
						}else{
							line = getAttributeName(a)+";"+m.getOperation().name()+";"+m.getAmount()+";"+m.getUniqueId();
							if(m.getSlot() != null) {
								line=line+";"+m.getSlot().name();
							}
							line=line+";custom_name:"+m.getName();
						}

						attributeList.add(line);
					}
				}

				return attributeList;
			}
		}
		return null;
	}

	public static ItemStack setAttributes(Codex plugin, ItemStack item, List<String> attributes) {
		if(attributes == null) {
			return item;
		}

		if(OtherUtils.isLegacy()) {
			return plugin.getNmsManager().setAttributes(item,attributes);
		}else {
			//1.13+
			ServerVersion serverVersion = Codex.serverVersion;
			boolean newSystem = serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R1);

			try{
				ItemMeta meta = item.getItemMeta();
				for(String a : attributes) {
					String[] sep = a.split(";");
					String attribute = sep[0];
					AttributeModifier.Operation op = AttributeModifier.Operation.valueOf(sep[1]);
					double amount = Double.valueOf(sep[2]);

					AttributeModifier modifier = null;

					if(newSystem){
						String[] id = sep[3].split(":");
						NamespacedKey namespacedKey = new NamespacedKey(id[0],id[1]);
						modifier = new AttributeModifier(namespacedKey,amount,op,EquipmentSlotGroup.getByName(sep[4]));
					}else{
						String customName = attribute;
						for(int i=0;i<sep.length;i++){
							if(sep[i].startsWith("custom_name:")){
								customName = sep[i].replace("custom_name:","");
							}
						}
						UUID uuid = UUID.fromString(sep[3]);
						if(sep.length >= 5) {
							if(!sep[4].startsWith("custom_name:")){
								EquipmentSlot slot = EquipmentSlot.valueOf(sep[4]);
								modifier = new AttributeModifier(uuid,customName,amount,op,slot);
							}else{
								modifier = new AttributeModifier(uuid,customName,amount,op);
							}
						}else {
							modifier = new AttributeModifier(uuid,customName,amount,op);
						}
					}

					meta.addAttributeModifier(getAttributeByName(attribute), modifier);
				}

				item.setItemMeta(meta);
			}catch(Exception e){

			}
		}
		return item;
	}

	public static void addDummyAttribute(ItemMeta meta,Codex plugin){
		try{
			AttributeModifier modifier = new AttributeModifier(new NamespacedKey(plugin,"dummy_attribute"),0,AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
			ServerVersion serverVersion = Codex.serverVersion;
			if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R2)){
				meta.addAttributeModifier(Attribute.GRAVITY, modifier);
			}else{
				meta.addAttributeModifier(getAttributeByName("GENERIC_GRAVITY"), modifier);
			}
		}catch(Exception ignored){

		}
	}

	public static CommonItemBookData getBookData(ItemStack item){
		CommonItemBookData bookData = null;
		String typeName = item.getType().name();

		List<String> pages = new ArrayList<String>();
		String author = null;
		String generation = null;
		String title = null;

		if(typeName.equals("WRITTEN_BOOK")) {
			BookMeta meta = (BookMeta) item.getItemMeta();

			author = meta.getAuthor();
			title = meta.getTitle();

			if(!Bukkit.getVersion().contains("1.12") && OtherUtils.isLegacy()) {
				pages = new ArrayList<String>(meta.getPages());
			}else {
				if(meta.getGeneration() != null) {
					generation = meta.getGeneration().name();
				}
				for(BaseComponent[] page : meta.spigot().getPages()) {
					pages.add(ComponentSerializer.toString(page));
				}
			}

			bookData = new CommonItemBookData(pages,author,generation,title);
			return bookData;
		}else {
			return null;
		}
	}

	public static void setBookData(ItemStack item,CommonItemBookData bookData){
		String typeName = item.getType().name();

		if(bookData == null) {
			return;
		}

		String author = bookData.getAuthor();
		String generation = bookData.getGeneration();
		String title = bookData.getTitle();
		List<String> pages = bookData.getPages();

		if(typeName.equals("WRITTEN_BOOK")) {
			BookMeta meta = (BookMeta) item.getItemMeta();
			if(!Bukkit.getVersion().contains("1.12") && OtherUtils.isLegacy()) {
				meta.setPages(new ArrayList<String>(pages));
			}else {
				ArrayList<BaseComponent[]> pagesBaseComponent = new ArrayList<BaseComponent[]>();
				for(String page : pages) {
					pagesBaseComponent.add(ComponentSerializer.parse(page));
				}
				meta.spigot().setPages(pagesBaseComponent);
				if(generation != null) {
					meta.setGeneration(Generation.valueOf(generation));
				}
			}
			meta.setAuthor(author);
			meta.setTitle(title);

			item.setItemMeta(meta);
		}
	}

	public static CommonItemTrimData getArmorTrimData(ItemStack item) {
		if(!OtherUtils.isTrimNew()) {
			return null;
		}

		String armorTrimPattern = null;
		String armorTrimMaterial = null;
		if(item.getItemMeta() instanceof ArmorMeta) {
			ArmorMeta meta = (ArmorMeta) item.getItemMeta();
			if(meta.hasTrim()){
				ArmorTrim armorTrim = meta.getTrim();
				armorTrimPattern = armorTrim.getPattern().getKey().getKey();
				armorTrimMaterial = armorTrim.getMaterial().getKey().getKey();
			}else{
				return null;
			}
		}else{
			return null;
		}

		return new CommonItemTrimData(armorTrimPattern,armorTrimMaterial);
	}

	public static void setArmorTrimData(ItemStack item,CommonItemTrimData trimData){
		if(trimData == null || !OtherUtils.isTrimNew()) {
			return;
		}

		String pattern = trimData.getPattern();
		String material = trimData.getMaterial();

		if(item.getItemMeta() instanceof ArmorMeta) {
			ArmorMeta meta = (ArmorMeta) item.getItemMeta();
			if(pattern != null && material != null){
				ArmorTrim armorTrim = new ArmorTrim(
						Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(material.toLowerCase())),
						Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(pattern.toLowerCase()))
				);
				meta.setTrim(armorTrim);
				item.setItemMeta(meta);
			}
		}
	}

	private static String getBannerPatternName(Pattern p) {
		try {
			Object patternType = p.getPattern();
			Method getPatternName = patternType.getClass().getMethod("name");
			getPatternName.setAccessible(true);
			return (String) getPatternName.invoke(patternType);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static PatternType getBannerPatternByName(String name){
		try {
			Class<?> patternTypeClass = Class.forName("org.bukkit.block.banner.PatternType");
			Method valueOf = patternTypeClass.getMethod("valueOf", String.class);
			return (PatternType) valueOf.invoke(null,name);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static Attribute getAttributeByName(String name){
		try {
			Class<?> attributeTypeClass = Class.forName("org.bukkit.attribute.Attribute");
			Field field = attributeTypeClass.getField(name);
			return (Attribute) field.get(null);
		} catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getAttributeName(Object attribute){
		try {
			Class<?> attributeTypeClass = Class.forName("org.bukkit.attribute.Attribute");
			return (String)attributeTypeClass.getMethod("name").invoke(attribute);
		} catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
