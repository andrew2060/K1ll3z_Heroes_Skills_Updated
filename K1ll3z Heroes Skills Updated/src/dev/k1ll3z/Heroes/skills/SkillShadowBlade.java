package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Setting;
import java.util.Iterator;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillShadowBlade extends ActiveSkill
{
  public SkillShadowBlade(Heroes plugin)
  {
    super(plugin, "ShadowBlade");
    setDescription("Damages enemies around your for %1 damage and silences them as well!");
    setUsage("/skill shadowblade");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill shadowblade" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DARK });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.RADIUS.node(), Integer.valueOf(10));
    node.set(Setting.DAMAGE.node(), Integer.valueOf(10));
    node.set(Setting.DURATION.node(), Integer.valueOf(5000));
    return node;
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 5000, false);
    SilenceEffect sEffect = new SilenceEffect(this, duration);
    int radius = SkillConfigManager.getUseSetting(hero, this, Setting.RADIUS, 10, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity entity = (Entity)i$.next();
      if ((entity instanceof LivingEntity));
      LivingEntity target = (LivingEntity)entity;
      if (!target.equals(player))
      {
        int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
        addSpellTarget(target, hero);
        damageEntity(target, player, damage, EntityDamageEvent.DamageCause.LIGHTNING);
      }

    }

    while (i$.hasNext())
    {
      Entity n = (Entity)i$.next();
      if ((n instanceof Player)) {
        Player p = (Player)n;
        Hero tHero = this.plugin.getCharacterManager().getHero(p);
        tHero.addEffect(sEffect);
      }
    }
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 5000, false);
    int radius = SkillConfigManager.getUseSetting(hero, this, Setting.RADIUS, 20, false);
    int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
    return getDescription().replace("$1", damage);
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillShadowBlade
 * JD-Core Version:    0.6.2
 */