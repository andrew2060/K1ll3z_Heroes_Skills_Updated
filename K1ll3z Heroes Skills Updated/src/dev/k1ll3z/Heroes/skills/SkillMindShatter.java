package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicDamageEffect;
import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
import com.herocraftonline.heroes.characters.effects.common.StunEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillMindShatter extends TargettedSkill
{
  private String applyText;
  private String expireText;

  public SkillMindShatter(Heroes plugin)
  {
    super(plugin, "MindShatter");
    setDescription("Silences, stuns and causes your target damage over $1 seconds.");
    setUsage("/skill mindshatter");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill mindshatter" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.DEBUFF });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.DURATION.node(), Integer.valueOf(3000));
    node.set(SkillSetting.PERIOD.node(), Integer.valueOf(1000));
    node.set("tick-damage", Integer.valueOf(3));
    node.set(SkillSetting.AMOUNT.node(), Integer.valueOf(20));
    node.set(SkillSetting.APPLY_TEXT.node(), "%target%'s mind has been shattered!");
    node.set(SkillSetting.EXPIRE_TEXT.node(), "%target% has regained their mind!");
    return node;
  }

  public void init()
  {
    super.init();
    this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT.node(), "%target%'s mind has been shattered!").replace("%target%", "$1");
    this.expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT.node(), "%target% has regained their mind!").replace("%target%", "$1");
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();
    long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 3000, false);
    long period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD, 1000, true);
    int tickDamage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 3, false);
    SilenceEffect sEffect = new SilenceEffect(this, duration);
    StunEffect tEffect = new StunEffect(this, duration);
    BleedSkillEffect bEffect = new BleedSkillEffect(this, duration, period, tickDamage, player);
    if ((target instanceof Player))
      this.plugin.getCharacterManager().getHero((Player)target).addEffect(sEffect);
    this.plugin.getCharacterManager().getHero((Player)target).addEffect(tEffect);
    this.plugin.getCharacterManager().getHero((Player)target).addEffect(bEffect);
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 3, false);
    return getDescription().replace("$1", duration + "");
  }

  public class BleedSkillEffect extends PeriodicDamageEffect
  {
    public BleedSkillEffect(Skill skill, long duration, long period, int tickDamage, Player applier)
    {
      super(skill,"Bleed", period, duration, tickDamage, applier);
      this.types.add(EffectType.BLEED);
    }

    public void apply(LivingEntity lEntity) {
      super.apply((CharacterTemplate)lEntity);
    }

    public void apply(Hero hero)
    {
      super.apply(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), SkillMindShatter.this.applyText, new Object[] { player.getDisplayName() });
    }

    public void remove(LivingEntity lEntity) {
      super.remove((CharacterTemplate)lEntity);
      broadcast(lEntity.getLocation(), SkillMindShatter.this.expireText, new Object[] { Messaging.getLivingEntityName(lEntity).toLowerCase() });
    }

    public void remove(Hero hero)
    {
      super.remove(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), SkillMindShatter.this.expireText, new Object[] { player.getDisplayName() });
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillMindShatter
 * JD-Core Version:    0.6.2
 */