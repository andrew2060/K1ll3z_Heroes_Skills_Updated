package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.common.StunEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillLullaby extends TargettedSkill
{
  private String applyText;
  private String expireText;

  public SkillLullaby(Heroes plugin)
  {
    super(plugin, "Lullaby");
    setDescription("You sing your target to sleep!");
    setUsage("/skill lullaby <target>");
    setArgumentRange(0, 1);
    setIdentifiers(new String[] { 
      "skill lullaby" });

    setTypes(new SkillType[] { 
      SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.DEBUFF });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(4));
    node.set(SkillSetting.DURATION.node(), Integer.valueOf(6000));
    return node;
  }

  public void init()
  {
    super.init();
    this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT.node(), "%hero% has sung a lullaby!").replace("%hero%", "$1");
    this.expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT.node(), "%hero%'s lullaby wore off!").replace("%hero%", "$1");
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();
    int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 6000, false);
    StunEffect tEffect = new StunEffect(this, duration);
    this.plugin.getCharacterManager().getHero((Player)target).addEffect(new BloodlustEffect(this, duration, this.applyText, this.expireText));
    this.plugin.getCharacterManager().getHero((Player)target).addEffect(tEffect);
    broadcastExecuteText(hero, target);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 4, false);
    return getDescription().replace("$1", damage + "");
  }

  public class BloodlustEffect extends ExpirableEffect
  {
    private String applyText;
    private String expireText;

    public BloodlustEffect(Skill skill, long duration, String applyText, String expireText)
    {
      super(skill,"Bloodlust", duration);
      this.applyText = applyText;
      this.expireText = expireText;
      this.types.add(EffectType.DISPELLABLE);
      this.types.add(EffectType.BENEFICIAL);
      this.types.add(EffectType.PHYSICAL);
      addMobEffect(15, (int)(duration / 1000L * 20L), 2, false);
      addMobEffect(16, (int)(duration / 1000L * 20L), 2, false);
    }

    public void apply(Hero hero) {
      super.apply(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), this.applyText, new Object[] { player.getDisplayName() });
    }

    public void remove(Hero hero) {
      super.remove(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), this.expireText, new Object[] { player.getDisplayName() });
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillLullaby
 * JD-Core Version:    0.6.2
 */