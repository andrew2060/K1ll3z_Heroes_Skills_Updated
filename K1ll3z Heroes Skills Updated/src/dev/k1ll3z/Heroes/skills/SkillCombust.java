package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.CombustEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Iterator;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillCombust extends ActiveSkill
{
  public SkillCombust(Heroes plugin)
  {
    super(plugin, "Combust");
    setDescription("You warm the are around you with your blade and cause enemies around you to burn!");
    setUsage("/skill combust");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill combust" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DEBUFF });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(10));
    node.set("fire-ticks", Integer.valueOf(100));
    return node;
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 10, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity entity = (Entity)i$.next();
      if ((entity instanceof LivingEntity));
      LivingEntity target = (LivingEntity)entity;
      if (!target.equals(player))
      {
        entity.setFireTicks(SkillConfigManager.getUseSetting(hero, this, "fire-ticks", 100, false));
        if ((entity instanceof Player)) {
          this.plugin.getCharacterManager().getHero((Player)entity).addEffect(new CombustEffect(this, player));
        }
      }
    }

    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 10, false);
    return getDescription();
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillCombust
 * JD-Core Version:    0.6.2
 */