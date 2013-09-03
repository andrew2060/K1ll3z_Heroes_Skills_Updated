package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.util.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SkillWhirlWind extends ActiveSkill
{
  private String applyText;
  private String expireText;

  public SkillWhirlWind(Heroes plugin)
  {
    super(plugin, "WhirlWind");
    setDescription("Disarms enemies around you!");
    setUsage("/skill whirlwind");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill whirlwind" });
    setTypes(new SkillType[] { SkillType.DEBUFF, SkillType.COUNTER });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.RADIUS.node(), Integer.valueOf(5));
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(5));
    node.set(SkillSetting.APPLY_TEXT.node(), "%target% is disarmed!");
    node.set(SkillSetting.EXPIRE_TEXT.node(), "%target% is no longer disarmed!");
    return node;
  }

  public void init()
  {
    super.init();
    this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT.node(), "%target% is disarmed!").replace("%target%", "$1");
    this.expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT.node(), "%target% is no longer disarmed!").replace("%target%", "$1");
  }

  public SkillResult use(Hero hero, String[] args)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 5, false);
    Player player = hero.getPlayer();
    int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 5, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 5000, false);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity n = (Entity)i$.next();
      if ((n instanceof Player)) {
        Player p = (Player)n;
        Hero tHero = this.plugin.getCharacterManager().getHero(p);
        tHero.addEffect(new DisarmEffect(this, duration, this.applyText, this.expireText));
      }
      else
      {
        damageEntity((LivingEntity)n, player, damage, DamageCause.MAGIC);
      }
    }
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 20, false);
    return getDescription().replace("$1", amount + "");
  }

  public class DisarmEffect extends ExpirableEffect
  {
    private final String applyText;
    private final String expireText;
    private HashMap<Hero, ItemStack[]> disarms = new HashMap();

    public DisarmEffect(Skill skill, long duration, String applyText, String expireText) {
      super(skill,"Disarm", duration);
      this.types.add(EffectType.HARMFUL);
      this.types.add(EffectType.DISARM);
      this.applyText = applyText;
      this.expireText = expireText;
    }

    public void apply(Hero hero) {
      super.apply(hero);
      Player player = hero.getPlayer();
      ItemStack[] inv = player.getInventory().getContents();
      for (int i = 0; i < 9; i++) {
        ItemStack is = inv[i];
        if ((is != null) && (Util.isWeapon(is.getType()))) {
          if (!this.disarms.containsKey(hero)) {
            ItemStack[] disarmedItems = new ItemStack[9];
            disarmedItems[i] = is.clone();
            this.disarms.put(hero, disarmedItems);
            player.getInventory().clear(i);
          } else {
            ItemStack[] items = (ItemStack[])this.disarms.get(hero);
            items[i] = is;
            player.getInventory().clear(i);
          }
        }
      }
      Util.syncInventory(player, this.plugin);
      broadcast(player.getLocation(), this.applyText, new Object[] { player.getDisplayName() });
    }

    public void remove(Hero hero) {
      super.remove(hero);
      Player player = hero.getPlayer();

      if (this.disarms.containsKey(hero)) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();
        ItemStack[] oldInv = (ItemStack[])this.disarms.get(hero);
        for (int i = 0; i < 9; i++) {
          if (oldInv[i] != null) {
            if (contents[i] != null) {
              Util.moveItem(hero, i, contents[i]);
            }
            inv.setItem(i, oldInv[i]);
          }
        }
        this.disarms.remove(hero);
        Util.syncInventory(player, this.plugin);
      }
      broadcast(player.getLocation(), this.expireText, new Object[] { player.getDisplayName() });
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillWhirlWind
 * JD-Core Version:    0.6.2
 */