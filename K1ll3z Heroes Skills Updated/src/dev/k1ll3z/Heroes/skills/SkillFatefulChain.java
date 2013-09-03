package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillFatefulChain extends ActiveSkill
{
  public SkillFatefulChain(Heroes plugin)
  {
    super(plugin, "FatefulChain");
    setDescription("You reciprocate any damage done to you to nearby enemies.");
    setUsage("/skill fatefulchain");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill fatefulchain" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(6));
    node.set(SkillSetting.DURATION.node(), Integer.valueOf(5000));
    return node;
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 5000, false);
    hero.addEffect(new FatefulChainEffect(this, duration));
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 10, false);
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 6, false);
    return getDescription().replace("$1", damage + "");
  }

  public class FatefulChainEffect extends ExpirableEffect
  {
    public FatefulChainEffect(Skill skill, long duration)
    {
      super(skill, "FatefulChainEffect", duration);
      this.types.add(EffectType.BENEFICIAL);
      this.types.add(EffectType.DISPELLABLE);
    }

    public void applyToHero(Hero hero)
    {
      super.applyToHero(hero);
      Player player = hero.getPlayer();
      double ldamage = player.getLastDamage();
      int radius = SkillConfigManager.getUseSetting(hero, SkillFatefulChain.this, SkillSetting.RADIUS, 10, false);
      List entities = player.getNearbyEntities(radius, radius, radius);
      Iterator i$ = entities.iterator();

      while (i$.hasNext())
      {
        Entity entity = (Entity)i$.next();
        if ((entity instanceof LivingEntity));
        LivingEntity target = (LivingEntity)entity;
        SkillFatefulChain.this.addSpellTarget(target, hero);
        SkillFatefulChain.damageEntity(target, player, ldamage, DamageCause.MAGIC);
      }
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillFatefulChain
 * JD-Core Version:    0.6.2
 */