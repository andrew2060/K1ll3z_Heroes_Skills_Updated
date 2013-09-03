package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SkillFireskin extends ActiveSkill
{
  private String applyText;
  private String expireText;

  public SkillFireskin(Heroes plugin)
  {
    super(plugin, "Fireskin");
    setDescription("Toggleable skill to resist lava and fire!");
    setUsage("/skill fireskin");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill fireskin" });
    setTypes(new SkillType[] { SkillType.BUFF, SkillType.FIRE });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.APPLY_TEXT.node(), "%hero% has fireskin!");
    node.set(SkillSetting.EXPIRE_TEXT.node(), "%hero% no longer has fireskin!");
    return node;
  }

  public void init()
  {
    super.init();
    this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT.node(), "%hero% has fireskin!").replace("%hero%", "$1");
    this.expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT.node(), "%hero% no longer has fireskin!").replace("%hero%", "$1");
  }

  public SkillResult use(Hero hero, String[] args)
  {
    if (hero.hasEffect("Fireskin"))
    {
      hero.removeEffect(hero.getEffect("Fireskin"));
    }
    else {
      FireskinEffect beffect = new FireskinEffect(this, this.applyText, this.expireText);
      hero.addEffect(beffect);
    }
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 20, false);
    return getDescription().replace("$1", amount + "");
  }

  public class FireskinEffect extends Effect
  {
    private String applyText;
    private String expireText;

    public FireskinEffect(Skill skill, String applyText, String expireText)
    {
      super(skill, "Fireskin");
      this.applyText = applyText;
      this.expireText = expireText;
      this.types.add(EffectType.RESIST_FIRE);
      this.types.add(EffectType.BENEFICIAL);
      this.types.add(EffectType.DISPELLABLE);
      addMobEffect(12, 1, 1, false);
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
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillFireskin
 * JD-Core Version:    0.6.2
 */