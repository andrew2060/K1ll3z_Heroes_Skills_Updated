package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicDamageEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Setting;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillBloodStorm extends ActiveSkill
{
  private String applyText;
  private String expireText;

  public SkillBloodStorm(Heroes plugin)
  {
    super(plugin, "BloodStorm");
    setDescription("You cause enemies around you to bleed!");
    setUsage("/skill bloodstorm");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill bloodstorm", "skill bstorm" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DEBUFF });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.RADIUS.node(), Integer.valueOf(10));
    node.set(Setting.DURATION.node(), Integer.valueOf(10000));
    node.set(Setting.PERIOD.node(), Integer.valueOf(2000));
    node.set(Setting.DAMAGE.node(), Integer.valueOf(10));
    node.set("tick-damage", Integer.valueOf(1));
    node.set(Setting.APPLY_TEXT.node(), "%target% is bleeding!");
    node.set(Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!");
    return node;
  }

  public void init()
  {
    super.init();
    this.applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT.node(), "%target% is bleeding!").replace("%target%", "$1");
    this.expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!").replace("%target%", "$1");
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
    int radius = SkillConfigManager.getUseSetting(hero, this, Setting.RADIUS, 10, false);
    long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
    long period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 2000, true);
    int tickDamage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity entity = (Entity)i$.next();
      if ((entity instanceof LivingEntity));
      LivingEntity target = (LivingEntity)entity;

      if ((entity instanceof Player)) {
        this.plugin.getCharacterManager().getHero((Player)entity).addEffect(new BleedSkillEffect(this, duration, period, tickDamage, player));
      }
      else
      {
        damageEntity(target, player, damage, EntityDamageEvent.DamageCause.MAGIC);
      }
    }

    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    return getDescription();
  }

  public class BleedSkillEffect extends PeriodicDamageEffect
  {
    public BleedSkillEffect(Skill skill, long duration, long period, int tickDamage, Player applier)
    {
      super("Bleed", period, duration, tickDamage, applier);
      this.types.add(EffectType.BLEED);
    }

    public void apply(LivingEntity lEntity) {
      super.apply((CharacterTemplate)lEntity);
    }

    public void apply(Hero hero) {
      super.apply(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), SkillBloodStorm.this.applyText, new Object[] { player.getDisplayName() });
    }
    public void remove(LivingEntity lEntity) {
      super.remove((CharacterTemplate)lEntity);
      broadcast(lEntity.getLocation(), SkillBloodStorm.this.expireText, new Object[] { Messaging.getLivingEntityName(lEntity).toLowerCase() });
    }

    public void remove(Hero hero) {
      super.remove(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), SkillBloodStorm.this.expireText, new Object[] { player.getDisplayName() });
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillBloodStorm
 * JD-Core Version:    0.6.2
 */