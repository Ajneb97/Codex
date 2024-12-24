package cx.ajneb97.versions;

import cx.ajneb97.Codex;
import cx.ajneb97.utils.ServerVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NMSManager {


    private Version version;
    private ServerVersion serverVersion;
    private Codex plugin;

    public NMSManager(Codex plugin){
        this.plugin = plugin;
        this.version = new Version();
        this.serverVersion = Codex.serverVersion;

        if(serverVersionGreaterEqualThan(ServerVersion.v1_20_R4)){
            return;
        }

        try {
            //Classes
            version.addClass("CraftItemStack",Class.forName("org.bukkit.craftbukkit."+serverVersion+".inventory.CraftItemStack"));
            if(!serverVersionGreaterEqualThan(ServerVersion.v1_13_R1)) {
                //1.12 or lower
                version.addClass("NBTTagList",Class.forName("net.minecraft.server."+serverVersion+".NBTTagList"));
            }
            if(serverVersionGreaterEqualThan(ServerVersion.v1_17_R1)){
                //1.17 or greater
                version.addClass("ItemStackNMS",Class.forName("net.minecraft.world.item.ItemStack"));
                version.addClass("NBTTagCompound",Class.forName("net.minecraft.nbt.NBTTagCompound"));
                version.addClass("MojangsonParser",Class.forName("net.minecraft.nbt.MojangsonParser"));
                version.addClass("NBTBase",Class.forName("net.minecraft.nbt.NBTBase"));
            }else{
                //1.16 or lower
                version.addClass("ItemStackNMS",Class.forName("net.minecraft.server."+serverVersion+".ItemStack"));
                version.addClass("NBTTagCompound",Class.forName("net.minecraft.server."+serverVersion+".NBTTagCompound"));
                version.addClass("MojangsonParser",Class.forName("net.minecraft.server."+serverVersion+".MojangsonParser"));
                version.addClass("NBTBase",Class.forName("net.minecraft.server."+serverVersion+".NBTBase"));
            }

            //Methods
            version.addMethod("asNMSCopy",version.getClassRef("CraftItemStack").getMethod("asNMSCopy",ItemStack.class));
            version.addMethod("asBukkitCopy",version.getClassRef("CraftItemStack").getMethod("asBukkitCopy",version.getClassRef("ItemStackNMS")));
            if(!serverVersionGreaterEqualThan(ServerVersion.v1_13_R1)){
                //1.12 or lower
                version.addMethod("getList",version.getClassRef("NBTTagCompound").getMethod("getList",String.class,int.class));
                version.addMethod("listSize",version.getClassRef("NBTTagList").getMethod("size"));
                version.addMethod("listGet",version.getClassRef("NBTTagList").getMethod("get",int.class));
                version.addMethod("listAdd",version.getClassRef("NBTTagList").getMethod("add",version.getClassRef("NBTBase")));
            }
            if(!serverVersionGreaterEqualThan(ServerVersion.v1_18_R1)){
                //1.17 or lower
                version.addMethod("hasTag",version.getClassRef("ItemStackNMS").getMethod("hasTag"));
                version.addMethod("getTag",version.getClassRef("ItemStackNMS").getMethod("getTag"));
                version.addMethod("setTag",version.getClassRef("ItemStackNMS").getMethod("setTag",version.getClassRef("NBTTagCompound")));
                version.addMethod("setString",version.getClassRef("NBTTagCompound").getMethod("setString",String.class,String.class));
                version.addMethod("setBoolean",version.getClassRef("NBTTagCompound").getMethod("setBoolean",String.class,boolean.class));
                version.addMethod("setDouble",version.getClassRef("NBTTagCompound").getMethod("setDouble",String.class,double.class));
                version.addMethod("setInt",version.getClassRef("NBTTagCompound").getMethod("setInt",String.class,int.class));
                version.addMethod("set",version.getClassRef("NBTTagCompound").getMethod("set",String.class,version.getClassRef("NBTBase")));
                version.addMethod("hasKey",version.getClassRef("NBTTagCompound").getMethod("hasKey",String.class));
                version.addMethod("getString",version.getClassRef("NBTTagCompound").getMethod("getString",String.class));
                version.addMethod("getBoolean",version.getClassRef("NBTTagCompound").getMethod("getBoolean",String.class));
                version.addMethod("getInt",version.getClassRef("NBTTagCompound").getMethod("getInt",String.class));
                version.addMethod("getDouble",version.getClassRef("NBTTagCompound").getMethod("getDouble",String.class));
                version.addMethod("getCompound",version.getClassRef("NBTTagCompound").getMethod("getCompound",String.class));
                version.addMethod("get",version.getClassRef("NBTTagCompound").getMethod("get",String.class));
                version.addMethod("remove",version.getClassRef("NBTTagCompound").getMethod("remove",String.class));
                if(serverVersionGreaterEqualThan(ServerVersion.v1_13_R1)){
                    version.addMethod("getKeys",version.getClassRef("NBTTagCompound").getMethod("getKeys"));
                }else{
                    version.addMethod("getKeys",version.getClassRef("NBTTagCompound").getMethod("c"));
                }
                version.addMethod("hasKeyOfType",version.getClassRef("NBTTagCompound").getMethod("hasKeyOfType",String.class,int.class));
                version.addMethod("parse",version.getClassRef("MojangsonParser").getMethod("parse",String.class));
            }else{
                //1.18 or greater
                String methodName = null;
                switch(serverVersion){
                    case v1_18_R1: methodName = "r"; break;
                    case v1_18_R2: methodName = "s"; break;
                    case v1_19_R1:
                    case v1_19_R2:
                    case v1_19_R3: methodName = "t"; break;
                    case v1_20_R1:
                    case v1_20_R2:
                    case v1_20_R3: methodName = "u"; break;
                }
                version.addMethod("hasTag",version.getClassRef("ItemStackNMS").getMethod(methodName));

                methodName = null;
                switch(serverVersion){
                    case v1_18_R1: methodName = "s"; break;
                    case v1_18_R2: methodName = "t"; break;
                    case v1_19_R1:
                    case v1_19_R2:
                    case v1_19_R3: methodName = "u"; break;
                    case v1_20_R1:
                    case v1_20_R2:
                    case v1_20_R3: methodName = "v"; break;
                }
                version.addMethod("getTag",version.getClassRef("ItemStackNMS").getMethod(methodName));

                version.addMethod("setTag",version.getClassRef("ItemStackNMS").getMethod("c",version.getClassRef("NBTTagCompound")));
                version.addMethod("setString",version.getClassRef("NBTTagCompound").getMethod("a",String.class,String.class));
                version.addMethod("setBoolean",version.getClassRef("NBTTagCompound").getMethod("a",String.class,boolean.class));
                version.addMethod("setDouble",version.getClassRef("NBTTagCompound").getMethod("a",String.class,double.class));
                version.addMethod("setInt",version.getClassRef("NBTTagCompound").getMethod("a",String.class,int.class));
                version.addMethod("set",version.getClassRef("NBTTagCompound").getMethod("a",String.class,version.getClassRef("NBTBase")));
                version.addMethod("hasKey",version.getClassRef("NBTTagCompound").getMethod("e",String.class));
                version.addMethod("getString",version.getClassRef("NBTTagCompound").getMethod("l",String.class));
                version.addMethod("getBoolean",version.getClassRef("NBTTagCompound").getMethod("q",String.class));
                version.addMethod("getInt",version.getClassRef("NBTTagCompound").getMethod("h",String.class));
                version.addMethod("getDouble",version.getClassRef("NBTTagCompound").getMethod("k",String.class));
                version.addMethod("getCompound",version.getClassRef("NBTTagCompound").getMethod("p",String.class));
                version.addMethod("get",version.getClassRef("NBTTagCompound").getMethod("c",String.class));
                version.addMethod("remove",version.getClassRef("NBTTagCompound").getMethod("r",String.class));

                methodName = null;
                switch(serverVersion){
                    case v1_18_R1:
                    case v1_18_R2:
                    case v1_19_R1: methodName = "d"; break;
                    case v1_19_R2:
                    case v1_19_R3:
                    case v1_20_R1:
                    case v1_20_R2:
                    case v1_20_R3: methodName = "e"; break;
                }
                version.addMethod("getKeys",version.getClassRef("NBTTagCompound").getMethod(methodName));
                version.addMethod("hasKeyOfType",version.getClassRef("NBTTagCompound").getMethod("b",String.class,int.class));
                version.addMethod("parse",version.getClassRef("MojangsonParser").getMethod("a",String.class));
            }





        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ItemStack setTagStringItem(ItemStack item, String key, String value) {
        if(serverVersionGreaterEqualThan(ServerVersion.v1_20_R4)){
            ItemMeta meta = item.getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
            PersistentDataContainer p = meta.getPersistentDataContainer();
            p.set(namespacedKey, PersistentDataType.STRING,value);
            item.setItemMeta(meta);
            return item;
        }

        try {
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            Object compound = getNBTCompound(newItem);
            version.getMethodRef("setString").invoke(compound,key,value);
            version.getMethodRef("setTag").invoke(newItem,compound);
            return (ItemStack)version.getMethodRef("asBukkitCopy").invoke(null,newItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return item;
    }

    public String getTagStringItem(ItemStack item,String key) {
        if(serverVersionGreaterEqualThan(ServerVersion.v1_20_R4)){
            ItemMeta meta = item.getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
            PersistentDataContainer p = meta.getPersistentDataContainer();
            if(p.has(namespacedKey)){
                return p.get(namespacedKey,PersistentDataType.STRING);
            }
            return null;
        }

        try {
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            Object compound = getNBTCompound(newItem);
            if((boolean)version.getMethodRef("hasKey").invoke(compound,key)){
                return (String)version.getMethodRef("getString").invoke(compound,key);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack removeTagItem(ItemStack item, String key) {
        if(serverVersionGreaterEqualThan(ServerVersion.v1_20_R4)){
            ItemMeta meta = item.getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

            PersistentDataContainer p = meta.getPersistentDataContainer();
            if(p.has(namespacedKey)){
                p.remove(namespacedKey);
            }
            item.setItemMeta(meta);
            return item;
        }

        try {
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            Object compound = getNBTCompound(newItem);
            version.getMethodRef("remove").invoke(compound,key);
            version.getMethodRef("setTag").invoke(newItem,compound);
            return (ItemStack)version.getMethodRef("asBukkitCopy").invoke(null,newItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return item;
    }

    public List<String> getNBT(ItemStack item){
        List<String> nbtList = new ArrayList<>();
        try {
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            if((boolean)version.getMethodRef("hasTag").invoke(newItem,null)){
                Object compound = version.getMethodRef("getTag").invoke(newItem,null);
                Set<String> tags = (Set<String>) version.getMethodRef("getKeys").invoke(compound,null);
                Set<String> notTags = new HashSet<>(Arrays.asList(
                        "ench", "HideFlags", "display", "SkullOwner", "AttributeModifiers", "Enchantments",
                        "Damage", "CustomModelData", "Potion", "StoredEnchantments", "CustomPotionColor",
                        "CustomPotionEffects", "Fireworks", "Explosion", "pages", "title", "author", "resolved",
                        "generation", "Trim", "custom_potion_effects"
                ));
                for(String t : tags) {
                    if(!notTags.contains(t)) {
                        if(t.equals("BlockEntityTag") && !item.getType().name().contains("SHULKER")){
                            continue;
                        }
                        if((boolean)version.getMethodRef("hasKeyOfType").invoke(compound,t,1)) {
                            //boolean
                            nbtList.add(t+"|"+version.getMethodRef("getBoolean").invoke(compound,t)+"|boolean");
                        }else if((boolean)version.getMethodRef("hasKeyOfType").invoke(compound,t,3)) {
                            //int
                            nbtList.add(t+"|"+version.getMethodRef("getInt").invoke(compound,t)+"|int");
                        }else if((boolean)version.getMethodRef("hasKeyOfType").invoke(compound,t,6)) {
                            //double
                            nbtList.add(t+"|"+version.getMethodRef("getDouble").invoke(compound,t)+"|double");
                        }else if((boolean)version.getMethodRef("hasKeyOfType").invoke(compound,t,10)){
                            //Compound
                            nbtList.add(t+"|"+version.getMethodRef("getCompound").invoke(compound,t)+"|compound");
                        }else if((boolean)version.getMethodRef("hasKeyOfType").invoke(compound,t,8)) {
                            //String
                            nbtList.add(t+"|"+version.getMethodRef("getString").invoke(compound,t));
                        }else {
                            //Other
                            nbtList.add(t+"|"+version.getMethodRef("get").invoke(compound,t));
                        }
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nbtList;
    }

    public ItemStack setNBT(ItemStack item, List<String> nbtList) {
        if(nbtList == null || nbtList.isEmpty()) {
            return item;
        }

        try {
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            Object compound = getNBTCompound(newItem);
            for(int i=0;i<nbtList.size();i++) {
                String nbt = nbtList.get(i);
                String[] sep = nbtList.get(i).split("\\|");
                String id = sep[0];
                String type = sep[sep.length-1];

                if(type.equals("boolean")) {
                    version.getMethodRef("setBoolean").invoke(compound,sep[0],Boolean.parseBoolean(sep[1]));
                }else if(type.equals("double")) {
                    version.getMethodRef("setDouble").invoke(compound,sep[0],Double.parseDouble(sep[1]));
                }else if(type.equals("int")) {
                    version.getMethodRef("setInt").invoke(compound,sep[0],Integer.parseInt(sep[1]));
                }else if(type.equals("compound")) {
                    String finalNBT = nbt.replace(id+"|", "").replace("|compound", "");
                    Object compoundNew = version.getMethodRef("parse").invoke(null,finalNBT);
                    version.getMethodRef("set").invoke(compound,sep[0],compoundNew);
                }else {
                    version.getMethodRef("setString").invoke(compound,sep[0],nbt.replace(id+"|", ""));
                }

            }
            version.getMethodRef("setTag").invoke(newItem,compound);
            return (ItemStack)version.getMethodRef("asBukkitCopy").invoke(null,newItem);
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAttributes(ItemStack item) {
        try {
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            if((boolean)version.getMethodRef("hasTag").invoke(newItem,null)) {
                Object compound = version.getMethodRef("getTag").invoke(newItem, null);
                if((boolean)version.getMethodRef("hasKey").invoke(compound,"AttributeModifiers")){
                    List<String> attributeList = new ArrayList<>();
                    Object attributes = version.getMethodRef("getList").invoke(compound,"AttributeModifiers",10); //NBTTagList
                    for(int i=0;i<(int)version.getMethodRef("listSize").invoke(attributes);i++){
                        Object compoundI = version.getMethodRef("listGet").invoke(attributes,i);
                        String attributeName = (String)version.getMethodRef("getString").invoke(compoundI,"AttributeName");
                        String name = (String)version.getMethodRef("getString").invoke(compoundI,"Name");
                        double amount = (double)version.getMethodRef("getDouble").invoke(compoundI,"Amount");
                        int operation = (int)version.getMethodRef("getInt").invoke(compoundI,"Operation");
                        int uuidLeast = (int)version.getMethodRef("getInt").invoke(compoundI,"UUIDLeast");
                        int uuidMost = (int)version.getMethodRef("getInt").invoke(compoundI,"UUIDMost");
                        String all = attributeName+";"+name+";"+amount+";"+operation+";"+uuidLeast+";"+uuidMost;
                        if((boolean)version.getMethodRef("hasKey").invoke(compoundI,"Slot")) {
                            all = all+";"+version.getMethodRef("getString").invoke(compoundI,"Slot");
                        }

                        attributeList.add(all);
                    }

                    return attributeList;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack setAttributes(ItemStack item, List<String> attributeList) {
        try{
            Object newItem = version.getMethodRef("asNMSCopy").invoke(null,item); //ItemStackNMS
            Object compound = getNBTCompound(newItem);
            Object attributesList = version.getClassRef("NBTTagList").newInstance();
            for(int i=0;i<attributeList.size();i++){
                Object attributeCompound = version.getClassRef("NBTTagCompound").newInstance();

                String[] data = attributeList.get(i).split(";");
                String attributeName = data[0];
                String name = data[1];
                double amount = Double.valueOf(data[2]);
                int operation = Integer.valueOf(data[3]);
                int uuidLeast = Integer.valueOf(data[4]);
                int uuidMost = Integer.valueOf(data[5]);

                version.getMethodRef("setString").invoke(attributeCompound,"AttributeName",attributeName);
                version.getMethodRef("setString").invoke(attributeCompound,"Name",name);
                version.getMethodRef("setDouble").invoke(attributeCompound,"Amount",amount);
                version.getMethodRef("setInt").invoke(attributeCompound,"Operation",operation);
                version.getMethodRef("setInt").invoke(attributeCompound,"UUIDLeast",uuidLeast);
                version.getMethodRef("setInt").invoke(attributeCompound,"UUIDMost",uuidMost);
                if(data.length >= 7) {
                    String slot = data[6];
                    version.getMethodRef("setString").invoke(attributeCompound,"Slot",slot);
                }
                version.getMethodRef("listAdd").invoke(attributesList,attributeCompound);
            }

            version.getMethodRef("set").invoke(compound,"AttributeModifiers",attributesList);
            version.getMethodRef("setTag").invoke(newItem,compound);

            return (ItemStack)version.getMethodRef("asBukkitCopy").invoke(null,newItem);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Object getNBTCompound(Object newItem){
        try {
            boolean hasTag = (boolean)version.getMethodRef("hasTag").invoke(newItem,null);
            Object compound = hasTag ? version.getMethodRef("getTag").invoke(newItem,null) :
                    version.getClassRef("NBTTagCompound").newInstance();
            return compound;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }



    private boolean serverVersionGreaterEqualThan(ServerVersion version){
        return serverVersion.ordinal() >= version.ordinal();
    }
}
